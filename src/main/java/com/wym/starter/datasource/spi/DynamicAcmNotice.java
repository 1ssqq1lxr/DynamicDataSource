package com.wym.starter.datasource.spi;

/**
 * 数据源变更通知
 *
 * @author lxr
 * @create 2018-04-28 15:46
 **/
public interface DynamicAcmNotice {

    void notify(String siteCode);

}
