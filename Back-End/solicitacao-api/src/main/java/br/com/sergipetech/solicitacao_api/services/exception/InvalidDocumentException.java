package br.com.sergipetech.solicitacao_api.services.exception;

public class InvalidDocumentException extends RuntimeException {
    public InvalidDocumentException(String CpfCnpj) {
        super("Credencial invalida: "+ CpfCnpj);
    }
}
