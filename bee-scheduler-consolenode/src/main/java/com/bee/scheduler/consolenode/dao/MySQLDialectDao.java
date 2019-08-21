package com.bee.scheduler.consolenode.dao;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "spring.datasource.platform", havingValue = "mysql")
public class MySQLDialectDao extends DaoSupport {

}
