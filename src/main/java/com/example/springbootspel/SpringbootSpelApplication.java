package com.example.springbootspel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class SpringbootSpelApplication {

    @Value("#{20 + 20}")
    private Integer sum;

    @Value("#{20 eq 20}")
    private Boolean equal;

    @Value("#{'Kevin'.length()}")
    private Integer myNameLength;

    @Value("#{age.getValue()}")
    private int age;

    @Autowired
    private EtheriumService etheriumService;

    @Autowired
    private AccountService accountService;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSpelApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            ExpressionParser parser = new SpelExpressionParser();
            String randomPhrase =
                    parser.parseExpression("random number is #{T(java.lang.Math).random()}",
                            new TemplateParserContext()).getValue(String.class);

            System.out.println(randomPhrase);

            TemplateParserContext templateParserContext = new TemplateParserContext();
            Account account = new Account();
            account.age = 23;
            account.name = "kfbian";
            Customer customer = new Customer();
            customer.id = UUID.randomUUID();

//            String json = "hahaha my name is #{template_parameter.name} and my account name is #{accountService.getName()}";
            String json = "  {'name': '#{template_parameter.name}', 'balance': #{accountService.getBalance(customer.getId().toString()) } }";

            Map<String, Object> templateParameter = new HashMap<>();
            templateParameter.put("name", "kevin");
            Map<String, Object> templateExprParameter = new HashMap<>();
            templateExprParameter.put("template_parameter", templateParameter);
            templateExprParameter.put("accountService", accountService);
            templateExprParameter.put("customer", customer);



//            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(json, templateParserContext);
            StandardEvaluationContext context = new StandardEvaluationContext(templateExprParameter);


            context.addPropertyAccessor(new MapAccessor());
            System.out.println( exp.getValue(context, String.class) );

//            System.out.println(message);


        };
    }

    public class Customer {
        UUID id;
        public UUID getId(){
            return this.id;
        }
    }

    public class Account {
        String name;
        Integer age;

        public String getName(){
            return this.name;
        }
    }

    @Component("accountService")
    public static class AccountService {
        public BigDecimal getBalance(String customerId){
            System.out.println("ID "+ customerId);
            return BigDecimal.valueOf(2000);
        }
    }

    @Component
    public static class EtheriumService {
        public BigDecimal getBalance(String name){
            System.out.println("Name: "+name);
            return BigDecimal.valueOf(43343);
        }
    }



    @Component("age")
    public static class Age {
        private int value;

        public int getValue(){
            return 63;
        }

    }


}
