package test;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

//@Configuration
public class DataSourceConfiguration {

////    @Bean(destroyMethod = "", name = "EmbeddeddataSource")
//    public DataSource dataSource() {
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
//        dataSourceBuilder.url("jdbc:sqlite:" + "data/grt.vgw.db");
//        dataSourceBuilder.type(SQLiteDataSource.class);
//        return dataSourceBuilder.build();
//    }
}