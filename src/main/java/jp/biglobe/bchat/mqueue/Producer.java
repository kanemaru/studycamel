package jp.biglobe.bchat.mqueue;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Producer {
    public static void main(String[] args){
        new AnnotationConfigApplicationContext(ProducerConfig.class);
    }
}