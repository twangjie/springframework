package telnet.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import telnet.service.domain.Device;
import telnet.service.model.DeviceRepository;

/**
 * Created by 王杰 on 2017/7/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(ApplicationConfig.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private DeviceRepository userRepository;

    @Test
    public void test() throws Exception {

        // 创建10条记录
        userRepository.save(new Device("127.0.0.1", "22", "","","",(short)1));
        userRepository.save(new Device("127.0.0.1", "23", "","","",(short)1));


        // 测试findAll, 查询所有记录
        Assert.assertEquals(2, userRepository.findAll().size());


    }

}
