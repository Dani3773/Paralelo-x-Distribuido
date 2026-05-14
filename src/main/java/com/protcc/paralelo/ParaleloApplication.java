package com.protcc.paralelo;

import com.protcc.paralelo.paralelo.runner.SearchRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParaleloApplication {

    public static void main(String[] args) {
        SearchRunner.main(args);
    }

}
