package util;

public class FormatadorNumero {
    public static String formatar(long valor) {
        if (valor < 1000) {
            return String.valueOf(valor);
        }
        String[] sufixos = {"K", "M", "B", "T", "Qa", "Qi"};
        double numero = valor;
        int indice = -1;
        while (numero >= 1000 && indice < sufixos.length - 1) {
            numero /= 1000.0;
            indice++;
        }
        return String.format("%.1f%s", numero, sufixos[indice]);
    }
}