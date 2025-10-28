package util;

import javax.swing.text.MaskFormatter;

public final class CpfUtils {
    private CpfUtils() {}

    public static MaskFormatter cpfMask() {
        try {
            MaskFormatter mf = new MaskFormatter("###.###.###-##");
            mf.setPlaceholderCharacter('_');           
            mf.setValueContainsLiteralCharacters(false); 
            return mf;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar máscara de CPF", e);
        }
    }

    public static String unmask(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    public static boolean has11(String s) {
        return unmask(s).length() == 11;
    }
}
