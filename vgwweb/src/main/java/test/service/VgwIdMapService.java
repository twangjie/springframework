package test.service;

import test.domain.VgwIdMap;
import java.util.List;

public interface VgwIdMapService {

    List<VgwIdMap> getVgwIdMaps();

    List<VgwIdMap> getVgwIdMapsByType(Integer type);
}
