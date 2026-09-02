package br.com.sergipetech.solicitacao_api.services.utilities;

public class DocumentoValidator {

    public static boolean isValido(String documento) {

        if (documento == null) {
            return false;
        }

        if (documento.length() == 11) {
            return isCpfValido(documento);
        }

        if (documento.length() == 14) {
            return isCnpjValido(documento);
        }

        return false;
    }

    private static boolean isCpfValido(String cpf) {

        if (!cpf.matches("\\d{11}")) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int resto = soma % 11;
        int primeiroDigito = resto < 2 ? 0 : 11 - resto;

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        resto = soma % 11;
        int segundoDigito = resto < 2 ? 0 : 11 - resto;

        return primeiroDigito == Character.getNumericValue(cpf.charAt(9)) && segundoDigito == Character.getNumericValue(cpf.charAt(10));

    }

    private static boolean isCnpjValido(String cnpj) {

        if (!cnpj.matches("[A-Z0-9]{14}")) {
            return false;
        }

        if (!Character.isDigit(cnpj.charAt(12))
                || !Character.isDigit(cnpj.charAt(13))) {
            return false;
        }

        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigitoCnpj(cnpj.substring(0, 12));
        int segundoDigito = calcularDigitoCnpj(cnpj.substring(0, 12) + primeiroDigito);

        return primeiroDigito == Character.getNumericValue(cnpj.charAt(12)) && segundoDigito == Character.getNumericValue(cnpj.charAt(13));
    }

    private static int calcularDigitoCnpj(String base) {

        int[] pesos;

        if (base.length() == 12) {
            pesos = new int[]{
                    5, 4, 3, 2,
                    9, 8, 7, 6,
                    5, 4, 3, 2
            };
        } else {
            pesos = new int[]{
                    6, 5, 4, 3, 2,
                    9, 8, 7, 6, 5, 4, 3, 2
            };
        }

        int soma = 0;

        for (int i = 0; i < base.length(); i++) {
            soma += valorCnpj(base.charAt(i)) * pesos[i];
        }

        int resto = soma % 11;

        if (resto == 0 || resto == 1) {
            return 0;
        }

        return 11 - resto;
    }

    private static int valorCnpj(char caractere) {
        return caractere - 48;
    }
}