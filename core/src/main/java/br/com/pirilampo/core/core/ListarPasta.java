package br.com.pirilampo.core.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListarPasta {
    private static List<File> arquivos = new ArrayList<>();

    public static List<File> listarPasta(File curDir) throws Exception {
        arquivos = new ArrayList<>();

        listar(curDir);

        return arquivos;
    }

    private static void listar(File curDir) throws Exception {
        File[] filesList = curDir.listFiles();

        if(filesList != null) {
            for (File f : filesList) {
                if (f.isDirectory()) {
                    listar(f);
                }

                if (f.isFile() && ".feature".equalsIgnoreCase(Resource.getExtension(f))) {
                    arquivos.add(f);
                }
            }
        }else{
            throw new Exception("Pasta não localizada!");
        }
    }
}
