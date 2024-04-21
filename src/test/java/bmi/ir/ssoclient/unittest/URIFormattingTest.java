package bmi.ir.ssoclient.unittest;

import bmi.ir.ssoclient.config.UrlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class URIFormattingTest {
    @ParameterizedTest
    @MethodSource("Origin_Capturing_Source")
    void Test_Origin_Capturing(String highlyTrustedUri,String expectedOriginPart){
        String originPart = UrlUtils.getOriginPart(highlyTrustedUri);
        Assertions.assertEquals(expectedOriginPart,originPart);
    }

    static Stream<Arguments> Origin_Capturing_Source(){
        return Stream.of(
          Arguments.of("http://localhost:8080/mika/json","http://localhost:8080"),
          Arguments.of("https://ui.mika.org","https://ui.mika.org")
        );
    }

}
