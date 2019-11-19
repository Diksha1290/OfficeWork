package com.officework.ApiController;


public interface WebServiceInterface<T> {
    void apiResponse(T response);
    void apiError(T response);
    void serverError(Throwable t);
}
