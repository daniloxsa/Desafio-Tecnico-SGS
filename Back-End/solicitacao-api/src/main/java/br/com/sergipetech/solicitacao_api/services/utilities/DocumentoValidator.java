package br.com.sergipetech.solicitacao_api.services.utilities;


// fonte: https://www.campuscode.com.br/conteudos/o-calculo-do-digito-verificador-do-cpf-e-do-cnpj
public class DocumentoValidator {

    public static boolean isValido(String documento) {


        if (documento.length() == 11) {
            return isCpfValido(documento);
        }

        if (documento.length() == 14) {
            return isCnpjValido(documento);
        }
        return false;
    }

    private static boolean isCpfValido(String cpf) {


        if (cpf.length() != 11) {
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

        if (cnpj.length() != 14) {
            return false;
        }

        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }

        int soma = 0;
        int[] pesosPrimeiro = {
                5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
        };

        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
        }

        int resto = soma % 11;
        int primeiroDigito = resto < 2 ? 0 : 11 - resto;



        soma = 0;
        int[] pesosSegundo = {
                6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
        };

        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
        }

        resto = soma % 11;
        int segundoDigito = resto < 2 ? 0 : 11 - resto;


        return primeiroDigito == Character.getNumericValue(cnpj.charAt(12))
                && segundoDigito == Character.getNumericValue(cnpj.charAt(13));
    }
}