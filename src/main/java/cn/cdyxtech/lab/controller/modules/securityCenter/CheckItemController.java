package cn.cdyxtech.lab.controller.modules.securityCenter;

import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.ResponseBack;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.filter.MenuOperationFilter;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.util.TestRandomUtil;
import cn.cdyxtech.lab.vo.CheckDataVO;
import cn.cdyxtech.lab.vo.GradeConfigVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import com.emin.base.util.CommonsUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/checkItem")
public class CheckItemController extends BaseController {

    @Autowired
    private ConfigFacade configFacade;
    @Autowired
    private ECOFacade ecoFacade;
    @Autowired
    private MenuOperationFilter menuOperationFilter;

    @GetMapping("/index")
    public String index(Map<String,Object> data,Boolean toPh){
        Long topEcmId = JWTThreadLocalUtil.getRootEcmId();
        String operationCodes = menuOperationFilter.menuOperations("checkItem");
        List<CheckDataVO> voList =configFacade.getCheckData(topEcmId);
        Collections.sort(voList);
        ConfigOption.ConfigItem gradeConfig = configFacade.getConfigItem(ConfigOption.GRADE_CONFIG_GROUP_CODE,ConfigOption.GRADE_CONFIG_ITEM_CODE);
        if(gradeConfig!=null){
            GradeConfigVO vo = JSONObject.parseObject(gradeConfig.getValue(),GradeConfigVO.class);
            Map<String, String> colorMap = vo.getGradeColors().stream().collect(Collectors.toMap(GradeConfigVO.GradeColor::getGradeStr, GradeConfigVO.GradeColor::getColor));
            data.put("colorMap",colorMap);
        }

        data.put("checkItemList",voList);
        data.put("toPh",toPh==null?false:toPh);
        data.put("operationCodes",operationCodes);
        return "modules/security-center/checkConfig/index";
    }

    @GetMapping("/phList")
    public String phIndex(Map<String,Object> data,String operationCodes){
        List<ConfigOption.ConfigItem> items = configFacade.getConfigItems(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP);
        List<Integer> avalibleLevels = new ArrayList<>();
        Map<String, String> colorMap = new HashMap<>();
        ConfigOption.ConfigItem gradeConfig = configFacade.getConfigItem(ConfigOption.GRADE_CONFIG_GROUP_CODE,ConfigOption.GRADE_CONFIG_ITEM_CODE);
        if(gradeConfig!=null){
            GradeConfigVO vo = JSONObject.parseObject(gradeConfig.getValue(),GradeConfigVO.class);

            for(int i=1;i<=vo.getLimit();i++){
                avalibleLevels.add(i);
            }
            for(ConfigOption.ConfigItem item : items){
                List<Integer> usedList = Arrays.asList(CommonsUtil.stringToIntegerArr(item.getValue()));
                avalibleLevels = avalibleLevels.stream().filter(i -> !usedList.contains(i)).collect(toList());
            }
            colorMap = vo.getGradeColors().stream().collect(Collectors.toMap(GradeConfigVO.GradeColor::getGradeStr, GradeConfigVO.GradeColor::getColor));
            data.put("colorMap",colorMap);
        }
        for(ConfigOption.ConfigItem item : items){
            List<Integer> usedList = Arrays.asList(CommonsUtil.stringToIntegerArr(item.getValue()));
            avalibleLevels = avalibleLevels.stream().filter(i -> !usedList.contains(i)).collect(toList());
        }
        data.put("items",items);
        data.put("operationCodes",operationCodes);
        data.put("avalibleLevels",avalibleLevels);
        return "modules/security-center/checkConfig/potentialHazard/list";
    }

    @GetMapping("/phForm")
    public String phForm(Map<String,Object> data,String code,Integer[] avalibleLevels){
        List<Integer> levels = new ArrayList<>();
        if(avalibleLevels!=null){
            levels = new ArrayList<>(Arrays.asList(avalibleLevels));
        }

        if(StringUtils.isNotBlank(code)){
            ConfigOption.ConfigItem item = configFacade.getConfigItem(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP,code);
            List<Integer> usedList = Arrays.asList(CommonsUtil.stringToIntegerArr(item.getValue()));
            levels.addAll(usedList);
            data.put("ph",item);
        }

        data.put("levels",new HashSet<>(levels));
        return "modules/security-center/checkConfig/potentialHazard/form";
    }

    @GetMapping("/gradeConfig")
    public String gradeConfig(Map<String,Object> data){
        ConfigOption.ConfigItem item = configFacade.getConfigItem(ConfigOption.GRADE_CONFIG_GROUP_CODE,ConfigOption.GRADE_CONFIG_ITEM_CODE);
        data.put("itemCode",ConfigOption.GRADE_CONFIG_ITEM_CODE);
        data.put("item",item);
        return "modules/security-center/checkConfig/checkItem/grade";
    }

    @PostMapping("/saveGradeConfig")
    @ResponseBody
    public ResponseBack<Boolean> saveGradeConfig(@ModelAttribute ConfigOption.ConfigItem item){
        configFacade.saveConfigItem(ConfigOption.GRADE_CONFIG_GROUP_CODE,item);
        return ResponseBack.success(true);
    }

    @PostMapping("/savePh")
    @ResponseBody
    public ResponseBack<Boolean> savePh(@ModelAttribute ConfigOption.ConfigItem item){
        configFacade.saveConfigItem(ConfigOption.POTENTIAL_HAZARD_LEVEL_GROUP,item);
        return ResponseBack.success(true);
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseBack<List<CheckDataVO>> getCheckData(){
        Long topEcmId = JWTThreadLocalUtil.getRootEcmId();
        List<CheckDataVO> voList =configFacade.getCheckData(topEcmId);
        return ResponseBack.success(voList);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseBack<Boolean> save(@RequestBody String data){
        Long topEcmId =JWTThreadLocalUtil.getRootEcmId();
        configFacade.saveCheckData(topEcmId,data);
        return ResponseBack.success(true);
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseBack<Boolean> upload(MultipartFile file){
        try (InputStream fis = file.getInputStream();
             Workbook book = file.getOriginalFilename().endsWith("xls")?new HSSFWorkbook(fis):new XSSFWorkbook(fis);
        ) {
            TreeMap<String, JSONObject> levalMap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return TestRandomUtil.compareVersion(o2,o1);
                }
            });
            Sheet sheet = book.getSheetAt(0);
            int firstRow = 4;
            int rowCount = sheet.getLastRowNum();
            for (int i = firstRow; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                String seq = TestRandomUtil.getCellValue(row.getCell(0));
                String title = TestRandomUtil.getCellValue(row.getCell(1));
                String keyPoint = TestRandomUtil.getCellValue(row.getCell(2));
                String grade = TestRandomUtil.getCellValue(row.getCell(3));
                JSONObject item = new JSONObject();
                item.put("seq", seq);
                item.put("name", title);

                if(seq.matches("\\d+\\.\\d+\\.\\d+\\.?")){
                    item.put("hasChild",false);
                    item.put("keyPoint", keyPoint);
                    item.put("grade", grade);
                    item.put("level", 3);
                }
                else {
                    item.put("hasChild",true);
                    item.put("children", new JSONArray());
                }
                levalMap.put(seq, item);
            }
            Iterator<Map.Entry<String, JSONObject>> it = levalMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, JSONObject> entry = it.next();
                String key = entry.getKey();

                if(key.matches("\\d+\\.\\d+\\.\\d+\\.?")){
                    //第三层查找第二层
                    //截取前两段
                    if(key.endsWith(".")){
                        key = key.substring(0,key.length()-1);
                    }
                    String secondaryKey = key.substring(0,key.lastIndexOf("."));
                    JSONObject secondary = levalMap.get(secondaryKey);
                    secondary.put("level",2);
                    secondary.getJSONArray("children").add(0,entry.getValue());
                    it.remove();
                }
                if(key.matches("\\d+\\.\\d+\\.?")){
                    if(key.endsWith(".")){
                        key = key.substring(0,key.length()-1);
                    }
                    //第二层找第一层
                    //截取第一段
                    String primaryKey = key.substring(0,key.lastIndexOf("."));
                    JSONObject primary = levalMap.get(primaryKey);
                    primary.put("level",1);
                    primary.getJSONArray("children").add(0,entry.getValue());
                    it.remove();
                }

            }
            List<JSONObject> list = new ArrayList<>(levalMap.values());
            Collections.reverse(list);
            String data = JSONArray.toJSONString(list);
            return this.save(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseBack.error("文件解析失败");
    }

}
