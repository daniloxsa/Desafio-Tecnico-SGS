package br.com.sergipetech.solicitacao_api.services.exception;

public class StatusTransactionError extends RuntimeException {
    public StatusTransactionError(String message) {
        super(message);
    }
}
