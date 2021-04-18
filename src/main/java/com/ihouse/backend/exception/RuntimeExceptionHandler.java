package com.ihouse.backend.exception;

import com.ihouse.backend.vo.ResponseVo;
import com.ihouse.backend.enums.ResponseEnum;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

import static com.ihouse.backend.enums.ResponseEnum.ERROR;

@ControllerAdvice
public class RuntimeExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseVo handle(RuntimeException e) {
        return ResponseVo.error(ERROR, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseVo accessDeniedHandle() {
        return ResponseVo.error(ResponseEnum.ACCESS_DENIED);
    }

    @ExceptionHandler(FileEmptyException.class)
    @ResponseBody
    public ResponseVo fileEmptyHandle() {
        return ResponseVo.error(ResponseEnum.ACCESS_DENIED);
    }

    @ExceptionHandler(PictureNotFoundException.class)
    @ResponseBody
    public ResponseVo pictureNotFoundExceptionHandle() {
        return ResponseVo.error(ResponseEnum.PICTURE_NOT_FOUND);
    }

    @ExceptionHandler(ParamsNotValidException.class)
    @ResponseBody
    public ResponseVo paramsNotValidExceptionHandle() {
        return ResponseVo.error(ResponseEnum.PARAM_ERROR);
    }

    @ExceptionHandler( DataNotFoundException.class)
    @ResponseBody
    public ResponseVo dataNotFoundExceptionHandle() {
        return ResponseVo.error(ResponseEnum.DB_NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseVo IOHandle() {
        return ResponseVo.error(ResponseEnum.IO_Exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo notValidExceptionHandle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Objects.requireNonNull(bindingResult.getFieldError());
        return ResponseVo.error(ResponseEnum.PARAM_ERROR,
                bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
    }


}
