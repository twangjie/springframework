package telnet;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by 王杰 on 2017/7/25.
 */
@ConfigurationProperties(prefix = "webtelnet")
public class TelnetSettings {

    private String termType = "VT100";

    private Integer printCRLFTimes = 5;

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public Integer getPrintCRLFTimes() {
        return printCRLFTimes;
    }

    public void setPrintCRLFTimes(Integer printCRLFTimes) {
        this.printCRLFTimes = printCRLFTimes;
    }
}
