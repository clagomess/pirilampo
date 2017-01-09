package br.com.pirilampo.main;

import br.com.pirilampo.util.Compilador;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String SYS_PATH = "br/com/pirilampo/";
    static final String SYS_ICON = SYS_PATH + "resources/img_01.png";

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        logger.info("Pirilampo - Ver.: {}", main.getVersion());

        if(args.length > 0){
            CommandLine cmd = consoleOptions(args);
            Compilador compilador = new Compilador();

            if(cmd.getOptionValue("feature") == null && cmd.getOptionValue("feature_path") == null){
                logger.warn("É necessário informar {feature} ou {feature_path}");
                System.exit(1);
            }

            if(cmd.getOptionValue("feature") != null){
                compilador.compilarFeature(
                        cmd.getOptionValue("feature"),
                        cmd.getOptionValue("name"),
                        cmd.getOptionValue("version"),
                        cmd.getOptionValue("output")
                );
                System.exit(0);
            }

            if(cmd.getOptionValue("feature_path") != null){
                compilador.compilarPasta(
                        cmd.getOptionValue("feature_path"),
                        cmd.getOptionValue("feature_path_master"),
                        cmd.getOptionValue("name"),
                        cmd.getOptionValue("version"),
                        cmd.getOptionValue("output")
                );
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
            logger.info(e.getMessage());
            formatter.printHelp("Pirilampo", options);

            System.exit(1);
        }

        return cmd;
    }

    private synchronized String getVersion(){
        return getClass().getPackage().getImplementationVersion();
    }
}
