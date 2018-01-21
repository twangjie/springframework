package test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import test.domain.VgwIdMap;
import test.service.VgwIdMapService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@ResponseBody
@RequestMapping("api")
public class VgwIdMapController {

    private final VgwIdMapService vgwIdMapService;

    @Autowired
    public VgwIdMapController( VgwIdMapService vgwIdMapService) {
        this.vgwIdMapService = vgwIdMapService;
    }

    @RequestMapping(path = "/vgwidmap", method = RequestMethod.GET)
    public List<VgwIdMap> list(HttpServletRequest request, HttpServletResponse response) {

        String type = request.getParameter("type");
        if (type == null) {
            return vgwIdMapService.getVgwIdMaps();
        } else {
            return vgwIdMapService.getVgwIdMapsByType(Integer.parseInt(type));
        }
    }
}
