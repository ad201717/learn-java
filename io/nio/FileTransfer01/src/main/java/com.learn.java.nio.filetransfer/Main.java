package com.learn.java.nio.filetransfer;

import com.learn.java.nio.filetransfer.client.Client;
import com.learn.java.nio.filetransfer.server.Server;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        new Main().parseOptions(args);
    }

    public CommandLine parseOptions(String[] args) {
        Options options = new Options();
        options.addOption(new Option("h", false, "Print Help"));
        options.addOption(Option.builder("t").hasArg(true).desc("Service Type: s(server) c(default client)").required(true).build());

        options.addOption(Option.builder("s").hasArg(true).desc("Source File").required(false).build());
        options.addOption(Option.builder("c").hasArg(true).desc("ip:port").required(false).build());

        options.addOption(Option.builder("d").hasArg(true).desc("Destination Path").required(false).build());
        options.addOption(Option.builder("p").hasArg(true).desc("Port: Default 9999").required(false).build());

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(100);
        CommandLine commandLine = null;
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption("h")) {
                hf.printHelp("java -jar", options, true);
                System.exit(0);
            }

            //客户端
            if (!commandLine.hasOption("t") || "c".equals(commandLine.getOptionValue("t"))) {
                new Client().runClient(commandLine.getOptionValue("c"), commandLine.getOptionValue("s"));
            } else {
                new Server().runServer(Integer.parseInt(Optional.ofNullable(commandLine.getOptionValue("p")).orElse("9999")), commandLine.getOptionValue("d"));
            }
            return commandLine;
        } catch (ParseException e) {
            e.printStackTrace();
            hf.printHelp("java -jar", options, true);
            System.exit(0);
        }
        return null;
    }
}
