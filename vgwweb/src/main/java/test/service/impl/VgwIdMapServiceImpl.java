package test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.domain.VgwIdMap;
import test.domain.VgwIdMapRepository;
import test.service.VgwIdMapService;

import javax.transaction.Transactional;
import java.util.List;

@Component("VgwIdMapService")
@Transactional
public class VgwIdMapServiceImpl implements VgwIdMapService {

    @Autowired(required = false)
    private VgwIdMapRepository vgwIdMapRepository;

    @Override
    public List<VgwIdMap> getVgwIdMaps() {
        return vgwIdMapRepository.findAll();
    }

    @Override
    public List<VgwIdMap> getVgwIdMapsByType(Integer type) {
        return vgwIdMapRepository.findAllByType(type);
    }
}
