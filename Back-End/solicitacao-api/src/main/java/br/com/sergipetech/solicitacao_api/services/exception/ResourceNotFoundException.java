package br.com.sergipetech.solicitacao_api.services.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Object id) {
        super("O Recurso não foi encontrado. Id: "+ id);
    }
}
