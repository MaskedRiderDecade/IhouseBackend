package com.ihouse.backend.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Longs;
import com.ihouse.backend.config.ElasticSearchConfig;
import com.ihouse.backend.constants.RentValueBlock;
import com.ihouse.backend.domain.House;
import com.ihouse.backend.domain.HouseDetail;
import com.ihouse.backend.domain.HouseTag;
import com.ihouse.backend.exception.DataNotFoundException;
import com.ihouse.backend.form.RentSearch;
import com.ihouse.backend.repository.HouseDetailRepo;
import com.ihouse.backend.repository.HouseRepo;
import com.ihouse.backend.repository.HouseTagRepo;
import com.ihouse.backend.service.ServiceMultiResult;
import com.ihouse.backend.util.ConvertUtil;
import com.ihouse.backend.util.HouseSortUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//检索服务
@Service
@Slf4j
public class SearchService {

    private static final String INDEX_NAME="ihouse";

    private static final String INDEX_TYPE="house";

    @Autowired
    private HouseRepo houseRepo;

    @Autowired
    private HouseDetailRepo houseDetailRepo;

    @Autowired
    private HouseTagRepo houseTagRepo;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    public boolean index(Long houseId){
       House house=houseRepo.findById(houseId).orElseThrow(()->{
           log.error("house not valid!");
           throw new DataNotFoundException();
       });
        HouseDetail houseDetail=houseDetailRepo.findByHouseId(houseId);
        if(houseDetail==null){
            throw new DataNotFoundException();
        }
        HouseIndexTemplate houseIndexTemplate= ConvertUtil.convertHouseIndexTemplate(house,houseDetail);
        List<HouseTag>tags=houseTagRepo.findAllByHouseId(houseId);
        if(tags!=null&&!tags.isEmpty()){
            List<String>tagNames=new ArrayList<>();
            tags.forEach(houseTag ->tagNames.add(houseTag.getName()));
            houseIndexTemplate.setTags(tagNames);
        }

        SearchRequestBuilder requestBuilder=this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexConstants.HOUSE_ID,houseIndexTemplate.getHouseId()));
        log.debug(requestBuilder.toString());
        SearchResponse response=requestBuilder.get();
        long totalHits=response.getHits().getTotalHits();
        boolean success=false;
        if(totalHits==0){
            success=create(houseIndexTemplate);
        }else if(totalHits==1){
            String esId=response.getHits().getAt(0).getId();
            success=update(esId,houseIndexTemplate);
        }else{
            success=deleteAndCreate(totalHits,houseIndexTemplate);
        }
        if(success){
            log.debug("index success with houseId:{}",houseId);
        }
        return success;
    }

    public void createHouse(Long houseId){
        House house=houseRepo.findById(houseId).orElseThrow(()->{
            log.error("house not valid!");
            throw new DataNotFoundException();
        });
        HouseDetail houseDetail=houseDetailRepo.findByHouseId(houseId);
        if(houseDetail==null){
            throw new DataNotFoundException();
        }
        HouseIndexTemplate houseIndexTemplate= ConvertUtil.convertHouseIndexTemplate(house,houseDetail);
        List<HouseTag>tags=houseTagRepo.findAllByHouseId(houseId);
        if(tags!=null&&!tags.isEmpty()){
            List<String>tagNames=new ArrayList<>();
            tags.forEach(houseTag ->tagNames.add(houseTag.getName()));
            houseIndexTemplate.setTags(tagNames);
        }
        create(houseIndexTemplate);
    }

    private boolean create(HouseIndexTemplate houseIndexTemplate)  {
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME,INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON)
                    .get();
            log.debug("create house index with houseId:{}",houseIndexTemplate.getHouseId());
            if(response.status()== RestStatus.CREATED){
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("error to index house:{}",houseIndexTemplate.getHouseId());
            e.printStackTrace();
            return false;
        }
    }

    private boolean update(String esId,HouseIndexTemplate houseIndexTemplate){
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME,INDEX_TYPE,esId)
                    .setDoc(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON)
                    .get();
            log.debug("update house index with houseId:{}",houseIndexTemplate.getHouseId());
            if(response.status()== RestStatus.OK){
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("error to index house:{}",houseIndexTemplate.getHouseId());
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit,HouseIndexTemplate houseIndexTemplate){
        DeleteByQueryRequestBuilder builder=DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexConstants.HOUSE_ID,houseIndexTemplate.getHouseId()))
                .source(INDEX_NAME);
        BulkByScrollResponse response=builder.get();
        log.debug("delete by query for house:{}",builder);
        long deleted=response.getDeleted();
        if(deleted!=totalHit){
            log.warn("need delete {},but {} is deleted",totalHit,deleted);
            return  false;
        }
        return  create(houseIndexTemplate);

    }

    public ServiceMultiResult<Long> query(RentSearch rentSearch){

        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        boolQuery.filter(
                QueryBuilders.termQuery(HouseIndexConstants.CITY_EN_NAME,rentSearch.getCityEnName())
        );
        if(rentSearch.getRegionEnName()!=null&&!rentSearch.getRegionEnName().equals("*")){
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexConstants.REGION_EN_NAME,rentSearch.getRegionEnName())
            );
        }

        RentValueBlock area =RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if(!RentValueBlock.ALL.equals(area)){
            RangeQueryBuilder rangeQuery=QueryBuilders.rangeQuery(HouseIndexConstants.AREA);
            if(area.getMax()>0){
                rangeQuery.lte(area.getMax());
            }
            if(area.getMin()>0){
                rangeQuery.gte(area.getMin());
            }
            boolQuery.filter(rangeQuery);
        }

        RentValueBlock price =RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if(!RentValueBlock.ALL.equals(price)){
            RangeQueryBuilder rangeQueryBuilder=QueryBuilders.rangeQuery(HouseIndexConstants.PRICE);
            if(price.getMax()>0){
                rangeQueryBuilder.lte(price.getMax());
            }
            if(price.getMin()>0){
                rangeQueryBuilder.gte(price.getMin());
            }
            boolQuery.filter(rangeQueryBuilder);
        }

        if(rentSearch.getDirection()>0){
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.DIRECTION,rentSearch.getDirection()));
        }
        if(rentSearch.getRentWay()>-1){
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.RENT_WAY,rentSearch.getRentWay()));
        }


        boolQuery.must(QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
                HouseIndexConstants.TITLE,
                HouseIndexConstants.TRAFFIC,
                HouseIndexConstants.DISTRICT,
                HouseIndexConstants.SUBWAY_LINE_NAME,
                HouseIndexConstants.SUBWAY_STATION_NAME));



        int start=(rentSearch.getPage()-1)*rentSearch.getSize();
        SearchRequestBuilder searchRequestBuilder=this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSortUtil.getSortKey(rentSearch.getOrderBy()), SortOrder.fromString(rentSearch.getOrderDirection())
                ).setFrom(start).setSize(rentSearch.getSize());
        log.debug(searchRequestBuilder.toString());
        List<Long>houseIds=new ArrayList<>();
        SearchResponse response=searchRequestBuilder.get();
        if(response.status()!=RestStatus.OK){
            log.warn("search is not ok,{}",searchRequestBuilder.toString());
        }
        for(SearchHit hit:response.getHits()){
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexConstants.HOUSE_ID))));
        }
        return new ServiceMultiResult<>(houseIds.size(),houseIds);
    }

    public void remove(Long houseId){
        DeleteByQueryRequestBuilder builder=DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexConstants.HOUSE_ID,houseId))
                .source(INDEX_NAME);
        BulkByScrollResponse response=builder.get();
        log.debug("delete by query for house:{}",builder);
        long deleted=response.getDeleted();
        log.debug("delete total:{}",deleted);
    }

}
