package com.ihouse.backend.controller;

import com.google.gson.Gson;
import com.ihouse.backend.domain.SupportAddress;
import com.ihouse.backend.dto.HouseDto;
import com.ihouse.backend.dto.SupportAddressDto;
import com.ihouse.backend.exception.FileEmptyException;
import com.ihouse.backend.exception.ParamsNotValidException;
import com.ihouse.backend.exception.PictureNotFoundException;
import com.ihouse.backend.form.HouseForm;
import com.ihouse.backend.form.HouseSearch;
import com.ihouse.backend.service.ServiceMultiResult;
import com.ihouse.backend.service.ServiceResult;
import com.ihouse.backend.service.house.AddressService;
import com.ihouse.backend.service.house.HouseService;
import com.ihouse.backend.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private Gson gson;

    @Autowired
    private AddressService addressService;

    @Autowired
    private HouseService houseService;

    @PostMapping(value="admin/upload/photo",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseVo uploadPhoto(@RequestHeader(name = "Authorization") String authorization,@RequestBody MultipartFile file){
        if (file.isEmpty()) {
            throw new FileEmptyException();
        }

        String fileName = file.getOriginalFilename();
//        File target=new File("F:\\毕业设计\\backend\\backend\\src\\main\\resources\\tmp\\"+fileName);
        File target=new File(fileName);
        try{
            file.transferTo(target);
        } catch (IOException e) {
            throw new com.ihouse.backend.exception.IOException();
        }
        return ResponseVo.success();
    }

    @PostMapping("/admin/add/house")
    public ResponseVo addHouse(@RequestHeader(name = "Authorization") String authorization,@Valid @RequestBody HouseForm houseForm, BindingResult bindingResult){
        if(houseForm.getCover()==null){
            throw new PictureNotFoundException();
        }
        Map<SupportAddress.Level, SupportAddressDto> addressDtoMap= addressService.findCityAndRegion(houseForm.getCityEnName(),houseForm.getRegionEnName());
        if(addressDtoMap.keySet().size()!=2){
             throw new ParamsNotValidException();
        }
        ServiceResult<HouseDto>result=houseService.save(houseForm,authorization);
        if(result.isSuccess()){
            return ResponseVo.success(result.getResult());
        }
        throw new ParamsNotValidException();
    }

    @PostMapping("admin/houses")
    public ResponseVo houses(@RequestHeader(name = "Authorization")String authorization, @RequestBody HouseSearch houseSearch){
        ServiceMultiResult<HouseDto>result= houseService.adminQuery(houseSearch,authorization);
        return ResponseVo.success(result);
    }

    @GetMapping("admin/house/edit")
    public ResponseVo houseEditInfo(@RequestParam(value = "id") Long id){
       if(id==null||id<1){
           return ResponseVo.error();
       }
        ServiceResult<HouseDto> serviceResult = houseService.findCompleteOne(id);
       if(!serviceResult.isSuccess()){
           return ResponseVo.error();
       }
       return ResponseVo.success(serviceResult.getResult());
    }


    @PostMapping("admin/house/edit")
    public ResponseVo saveHouse(@RequestHeader(name = "Authorization")String authorization,@Valid @RequestBody HouseForm houseForm, BindingResult bindingResult) {
        Map<SupportAddress.Level, SupportAddressDto> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if (addressMap.keySet().size() != 2) {
            throw new ParamsNotValidException();
        }

        ServiceResult<HouseDto> result = houseService.update(houseForm,authorization);
        if (result.isSuccess()) {
            return ResponseVo.success();
        }

        return ResponseVo.error(result.getMessage());
    }

    //修改封面
    @PostMapping("admin/house/cover")
    public ResponseVo updateCover(@RequestParam(value = "cover") String cover,
                                   @RequestParam(value = "targetId") Long targetId) {
        ServiceResult result = this.houseService.updateCover(cover, targetId);

        if (result.isSuccess()) {
            return ResponseVo.success(result.getResult());
        } else {
            return ResponseVo.error(result.getMessage());
        }
    }

    //增加标签
    @PostMapping("admin/house/tag")
    public ResponseVo addHouseTag(@RequestParam(value = "houseId") Long houseId,
                                   @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || tag==null||tag.equals("")) {
            throw new ParamsNotValidException();
        }

        ServiceResult result = this.houseService.addTag(houseId, tag);
        if (result.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.error(result.getMessage());
        }
    }

    //删除标签
    @DeleteMapping("admin/house/tag")
    public ResponseVo removeHouseTag(@RequestParam(value = "houseId") Long houseId,
                                             @RequestParam(value = "tag") String tag) {
        if (houseId < 1 ||  tag==null||tag.equals("")) {
            throw new ParamsNotValidException();
        }

        ServiceResult result = this.houseService.removeTag(houseId, tag);
        if (result.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.error(result.getMessage());
        }
    }
}
