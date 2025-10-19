package com.barenne.insurance_manager_api.utils;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with id : " + id + " not found.");
    }
}
