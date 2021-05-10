package com.ihouse.backend.service.house;

import com.ihouse.backend.constants.RentValueBlock;
import com.ihouse.backend.domain.*;
import com.ihouse.backend.dto.HouseDetailDto;
import com.ihouse.backend.dto.HouseDto;
import com.ihouse.backend.dto.HousePictureDto;
import com.ihouse.backend.enums.HouseStatusEnum;
import com.ihouse.backend.exception.DataNotFoundException;
import com.ihouse.backend.form.HouseForm;
import com.ihouse.backend.form.HouseSearch;
import com.ihouse.backend.form.RentSearch;
import com.ihouse.backend.repository.*;
import com.ihouse.backend.service.ServiceMultiResult;
import com.ihouse.backend.service.ServiceResult;
import com.ihouse.backend.service.search.SearchService;
import com.ihouse.backend.service.user.UserUtil;
import com.ihouse.backend.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HouseService {

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private HouseRepo houseRepo;

    @Autowired
    private HouseDetailRepo houseDetailRepo;

    @Autowired
    private HousePictureRepo housePictureRepo;

    @Autowired
    private HouseTagRepo houseTagRepo;

    @Autowired
    private SubwayRepo subwayRepo;

    @Autowired
    private SubwayStationRepo subwayStationRepo;

    @Autowired
    private SearchService searchService;

//    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix="121.4.81.114/img/";

    @Transactional
    public ServiceResult<HouseDto>save(HouseForm houseForm,String token){

        House houseInput=House.builder()
                .title(houseForm.getTitle())
                .cityEnName(houseForm.getCityEnName())
                .regionEnName(houseForm.getRegionEnName())
                .street(houseForm.getStreet())
                .district(houseForm.getDistrict())
                .room(houseForm.getRoom())
                .parlour(houseForm.getParlour())
                .floor(houseForm.getFloor())
                .totalFloor(houseForm.getTotalFloor())
                .direction(houseForm.getDirection())
                .buildYear(houseForm.getBuildYear())
                .area(houseForm.getArea())
                .price(houseForm.getPrice())
                .distanceToSubway(houseForm.getDistanceToSubway())
                .cover(houseForm.getCover())
                .bathroom(houseForm.getBathroom())
                .build();
        Date now =new Date();
        houseInput.setCreateTime(now);
        houseInput.setLastUpdateTime(now);
        houseInput.setAdminId(userUtil.getUserId(token));
        House house=houseRepo.save(houseInput);

        HouseDetail houseDetail=saveHouseDetail(house,houseForm);

        List<String>tags=houseForm.getTags();
        if(tags!=null&&!tags.isEmpty()){
            List<HouseTag>houseTags=tags.stream().map(tag->{
                return HouseTag.builder().houseId(house.getId()).name(tag).build();
            }).collect(Collectors.toList());
            houseTagRepo.saveAll(houseTags);
        }

//        List<HousePicture> pictures=generateHousePictures(houseForm,house.getId());

//        List<HousePicture> housePictures=new ArrayList<>();

//        housePictureRepo.saveAll(pictures).forEach(housePictures::add);



//        List<HousePictureDto> housePictureDtos=ConvertUtil.convertHousePictureDto(housePictures);

//        HouseDto houseDto=ConvertUtil.convertHouseDto(house,houseDetailDto,housePictureDtos,tags);

        HouseDetailDto houseDetailDto= ConvertUtil.convertHouseDetailDto(houseDetail);

        HouseDto houseDto=ConvertUtil.convertHouseDto(house,houseDetailDto,null,tags);

        return new ServiceResult<HouseDto>(true,null,houseDto);


    }

    public ServiceMultiResult<HouseDto>adminQuery(HouseSearch houseSearch,String token){
        Long adminId=userUtil.getUserId(token);

        //确定分页和排序字段
        Sort sort=Sort.by(Sort.Direction.fromString(houseSearch.getDirection()),houseSearch.getOrderBy());
        Pageable pageable=PageRequest.of(houseSearch.getPage()-1,houseSearch.getPageSize(),sort);

        Page<House>houses;

        if(houseSearch.getCity()!=null){
            houses=houseRepo.findByAdminIdAndCityEnNameAndStatusIsNotOrderByCreateTimeDesc(
                    adminId,houseSearch.getCity(),HouseStatusEnum.DELETED.getValue(),pageable);
        }else{
            houses=houseRepo.findByAdminIdAndStatusIsNotOrderByCreateTimeDesc(adminId,HouseStatusEnum.DELETED.getValue(),pageable);
        }

        List<HouseDto> houseDtos=houses.getContent().stream().map(house->{
            HouseDto houseDto= ConvertUtil.convertHouseDto(house,adminId);
            houseDto.setCover(this.cdnPrefix+house.getCover());
            return houseDto;
        }).collect(Collectors.toList());

        return new ServiceMultiResult<>(houseDtos.size(),houseDtos);
    }

    public ServiceResult<HouseDto> findCompleteOne(Long id) {
        House house = houseRepo.findById(id).orElseThrow(()->{
            throw new DataNotFoundException();
        });
        HouseDetail houseDetail=houseDetailRepo.findByHouseId(id);
        List<HousePicture> pictures=housePictureRepo.findAllByHouseId(id);
        HouseDetailDto houseDetailDto=ConvertUtil.convertHouseDetailDto(houseDetail);
        List<HousePictureDto>housePictureDtos=ConvertUtil.convertHousePictureDto(pictures);
        List<HouseTag>tags=houseTagRepo.findAllByHouseId(id);
        List<String> tagStrs=ConvertUtil.convertHouseTagStr(tags);
        HouseDto houseDto=ConvertUtil.convertHouseDto(house,houseDetailDto,housePictureDtos,tagStrs);
        return new ServiceResult<>(houseDto);
    }

    @Transactional
    public ServiceResult<HouseDto>update(HouseForm houseForm,String token) {
        House house=houseRepo.findById(houseForm.getId()).orElseThrow(()->{
            throw new DataNotFoundException();
        });

        int originStatus=house.getStatus(),currStatus=houseForm.getStatus();

        HouseDetail houseDetail=houseDetailRepo.findByHouseId(house.getId());
        if(houseDetail==null){
            throw new DataNotFoundException();
        }
        updateHouseDetail(houseForm,houseDetail);

        updateHouse(houseForm,token,house);

        if(originStatus!=currStatus){
            //如果没有上架 现在上架
            if(currStatus==1){
                searchService.index(houseForm.getId());
                //如果本来已经上架，现在改为其它的状态
            }else if(originStatus==1){
                searchService.remove(houseForm.getId());
            }
        }
        //如果一直都在上架状态
        else if(originStatus==1){
            searchService.remove(houseForm.getId());
            searchService.createHouse(houseForm.getId());
        }
        return new ServiceResult<>();
    }

    @Transactional
    public ServiceResult updateCover(String cover, Long targetId) {
        House house=houseRepo.findById(targetId).orElseThrow(()->{
            throw new DataNotFoundException();
        });
        house.setCover(cover);

        houseRepo.save(house);
        return new ServiceResult(house);
    }

    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseRepo.findById(houseId).orElseThrow(()->{
            throw new DataNotFoundException();
        });

        HouseTag houseTag = houseTagRepo.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ServiceResult(false, "标签已存在");
        }

        houseTagRepo.save(new HouseTag(houseId, tag));
        return new ServiceResult();
    }

    @Transactional
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseRepo.findById(houseId).orElseThrow(()->{
            throw new DataNotFoundException();
        });

        HouseTag houseTag = houseTagRepo.findByNameAndHouseId(tag, houseId);
        if (houseTag == null) {
            return new ServiceResult(false, "标签不存在");
        }

        houseTagRepo.deleteById(houseTag.getId());
        return new ServiceResult();
    }



    private HouseDetail saveHouseDetail(House house,HouseForm houseForm){

        //查询地铁和地铁站
        Subway subway=subwayRepo.findById(houseForm.getSubwayLineId()).orElseThrow(()->{
            throw new DataNotFoundException();
        });

        SubwayStation subwayStation=subwayStationRepo.findById(houseForm.getSubwayStationId()).orElseThrow(()->{
            throw new DataNotFoundException();
        });

        if(!subway.getId().equals(subwayStation.getSubwayId())){
            throw new DataNotFoundException();
        }

        HouseDetail houseDetail=HouseDetail.builder()
                .houseId(house.getId())
                .description(houseForm.getDescription())
                .layoutDesc(houseForm.getLayoutDesc())
                .traffic(houseForm.getTraffic())
                .roundService(houseForm.getRoundService())
                .rentWay(houseForm.getRentWay())
                .detailAddress(houseForm.getDetailAddress())
                .subwayLineId(houseForm.getSubwayLineId())
                .subwayLineName(subway.getName())
                .subwayStationId(houseForm.getSubwayStationId())
                .subwayStationName(subwayStation.getName()).build();
        return houseDetailRepo.save(houseDetail);

    }

    private void updateHouseDetail(HouseForm houseForm,HouseDetail houseDetail){
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setTraffic(houseForm.getTraffic());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setSubwayLineId(houseForm.getSubwayLineId());
        houseDetail.setSubwayStationId(houseForm.getSubwayStationId());
        houseDetailRepo.save(houseDetail);
    }

    private House updateHouse(HouseForm houseForm,String token,House house){
        House houseInput=House.builder()
                .id(houseForm.getId())
                .title(houseForm.getTitle())
                .cityEnName(houseForm.getCityEnName())
                .regionEnName(houseForm.getRegionEnName())
                .street(houseForm.getStreet())
                .district(houseForm.getDistrict())
                .room(houseForm.getRoom())
                .parlour(houseForm.getParlour())
                .floor(houseForm.getFloor())
                .totalFloor(houseForm.getTotalFloor())
                .direction(houseForm.getDirection())
                .buildYear(houseForm.getBuildYear())
                .area(houseForm.getArea())
                .price(houseForm.getPrice())
                .distanceToSubway(houseForm.getDistanceToSubway())
                .cover(houseForm.getCover())
                .bathroom(houseForm.getBathroom())
                .createTime(house.getCreateTime())
                .watchTimes(houseForm.getWatchTimes())
                .build();
        Date now =new Date();
        houseInput.setLastUpdateTime(now);
        houseInput.setAdminId(userUtil.getUserId(token));
        if(houseForm.getStatus()!=null){
            houseInput.setStatus(houseForm.getStatus());
        }
        return houseRepo.save(houseInput);

    }

    private List<HouseDto>wrapperHouseResult(List<Long>houseIds){
        List<HouseDto>houseDtos=new ArrayList<>();
        Map<Long,HouseDto>idToHouseMap=new HashMap<>();
        List<House>houses=houseRepo.findAllByIdIn(houseIds);
        houses.forEach(house->{
            HouseDto houseDto=ConvertUtil.convertHouseDto(house);
            houseDto.setCover(this.cdnPrefix+house.getCover());
            idToHouseMap.put(house.getId(),houseDto);
        });

        wrapperHouseList(houseIds,idToHouseMap);

        for(Long houseId:houseIds){
            houseDtos.add(idToHouseMap.get(houseId));
        }
        return houseDtos;
    }

    private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDto> idToHouseMap) {
        List<HouseDetail> details = houseDetailRepo.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDto houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDto detailDTO = ConvertUtil.convertHouseDetailDto(houseDetail);
            houseDTO.setHouseDetail(detailDTO);
        });

        List<HouseTag> houseTags = houseTagRepo.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDto house = idToHouseMap.get(houseTag.getHouseId());
            house.getTags().add(houseTag.getName());
        });
    }

    public ServiceMultiResult<HouseDto> query(RentSearch rentSearch) {
        if (rentSearch.getKeywords() != null && !rentSearch.getKeywords().isEmpty()) {
            ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
            if (serviceResult.getTotal() == 0) {
                return new ServiceMultiResult<>(0, new ArrayList<>());
            }
            List<HouseDto>houseDtos=wrapperHouseResult(serviceResult.getResult());
            return new ServiceMultiResult<>(houseDtos.size(),houseDtos);
        }

        return simpleQuery(rentSearch);

    }

    public ServiceMultiResult<HouseDto> simpleQuery(RentSearch rentSearch) {
        Pageable pageable = PageRequest.of(rentSearch.getPage()-1, rentSearch.getSize());
        Page<House> houses;

        RentValueBlock area =RentValueBlock.matchArea(rentSearch.getAreaBlock());
        RentValueBlock price =RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        Specification<House>specification=(root,query,cb)->{
            Predicate predicate=cb.equal(root.get("cityEnName"),rentSearch.getCityEnName());
            predicate=cb.and(predicate,cb.equal(root.get("status"),1));
            if(!rentSearch.getRegionEnName().equals("*")){
                predicate=cb.and(predicate,cb.equal(root.get("regionEnName"),rentSearch.getRegionEnName()));
            }
            if(!RentValueBlock.ALL.equals(area)){
                if(area.getMin()>0){
                    predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("area"),area.getMin()));
                }
                if(area.getMax()>0){
                    predicate=cb.and(predicate,cb.lessThanOrEqualTo(root.get("area"),area.getMax()));
                }
            }
            if(!RentValueBlock.ALL.equals(price)){
                if(price.getMin()>0){
                    predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("price"),price.getMin()));
                }
                if(price.getMax()>0){
                    predicate=cb.and(predicate,cb.lessThanOrEqualTo(root.get("price"),price.getMax()));
                }
            }
            if(rentSearch.getDirection()>0){
                predicate=cb.and(predicate,cb.equal(root.get("direction"),rentSearch.getDirection()));
            }
            return predicate;
        };
        houses=houseRepo.findAll(specification,pageable);
        //默认以创建时间排序，另支持按价格，面积排序

        //将houseId与house实体类做映射
        List<Long>houseIds=new ArrayList<>();
        Map<Long,HouseDto>houseMap=new HashMap<>();
        List<HouseDto> houseDtos = new ArrayList<>(),res;

        for(House house:houses.getContent()){
            HouseDto houseDto=ConvertUtil.convertHouseDto(house);
            houseDto.setCover(this.cdnPrefix+house.getCover());
            houseDtos.add(houseDto);

            houseIds.add(house.getId());
            houseMap.put(house.getId(),houseDto);
        }
        List<HouseDetail>houseDetails=houseDetailRepo.findAllByHouseIdIn(houseIds);
        List<HouseTag>houseTags=houseTagRepo.findAllByHouseIdIn(houseIds);
        houseDetails.forEach(houseDetail -> {
            HouseDto houseDto=houseMap.get(houseDetail.getHouseId());
            houseDto.setHouseDetail(ConvertUtil.convertHouseDetailDto(houseDetail));
        });

        houseTags.forEach(houseTag -> {
            HouseDto houseDto=houseMap.get(houseTag.getHouseId());
            houseDto.getTags().add(houseTag.getName());
        });

        switch(rentSearch.getOrderBy()){
            case "price":res=houseDtos.stream().sorted(Comparator.comparing(HouseDto::getPrice)).collect(Collectors.toList());break;
            case "area":res=houseDtos.stream().sorted(Comparator.comparing(HouseDto::getArea)).collect(Collectors.toList());break;
            default:res=houseDtos.stream().sorted(Comparator.comparing(HouseDto::getLastUpdateTime)).collect(Collectors.toList());break;
        }
        if(rentSearch.getOrderDirection()!=null&&rentSearch.getOrderDirection().equals("desc")){
            Collections.reverse(res);
        }


        return new ServiceMultiResult<>(res.size(),res);
    }
}
