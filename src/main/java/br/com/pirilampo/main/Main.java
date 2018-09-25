package br.com.pirilampo.main;

import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.core.Compilador;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        log.info("Pirilampo - Ver.: {}", main.getVersion());

        if(args.length > 0){
            CommandLine cmd = consoleOptions(args);
            Compilador compilador = new Compilador();

            if(cmd.getOptionValue("feature") == null && cmd.getOptionValue("feature_path") == null){
                log.warn("É necessário informar {feature} ou {feature_path}");
                System.exit(1);
            }

            if(cmd.getOptionValue("feature") != null){
                compilador.compilarFeature(new Parametro(cmd));
                System.exit(0);
            }

            if(cmd.getOptionValue("feature_path") != null){
                compilador.compilarPasta(new Parametro(cmd));
                System.exit(0);
            }
        }else{
            MainUi.launch(MainUi.class);
        }
    }

    private static CommandLine consoleOptions(String[] args){
        Options options = new Options();
        Option option;

        options.addOption(new Option("feature", true, "Arquivo *.feature"));
        options.addOption(new Option("feature_path", true, "Diretório contendo arquivos *.feature"));
        options.addOption(new Option("feature_path_master", true, "Diretório contendo arquivos *.feature master"));
        options.addOption(new Option("output", true, "Diretório de saída"));

        option = new Option("name", true, "Nome do projeto");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("version", true, "Versão");
        option.setRequired(true);
        options.addOption(option);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.info(e.getMessage());
            formatter.printHelp("Pirilampo", options);

            System.exit(1);
        }

        return cmd;
    }

    private synchronized String getVersion(){
        return getClass().getPackage().getImplementationVersion();
    }
}
