package com.sippy.wrapper.parent;

import com.sippy.wrapper.parent.database.DatabaseConnection;
import com.sippy.wrapper.parent.database.dao.TnbDao;
import com.sippy.wrapper.parent.request.JavaTestRequest;
import com.sippy.wrapper.parent.request.TnbListRequest;
import com.sippy.wrapper.parent.response.JavaTestResponse;

import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.sippy.wrapper.parent.response.TnbListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class WrappedMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(WrappedMethods.class);

    @EJB
    DatabaseConnection databaseConnection;

    @RpcMethod(name = "javaTest", description = "Check if everything works :)")
    public Map<String, Object> javaTest(JavaTestRequest request) {
        JavaTestResponse response = new JavaTestResponse();

        int count = databaseConnection.getAllTnbs().size();

        LOGGER.info("the count is: " + count);

        response.setId(request.getId());
        String tempFeeling = request.isTemperatureOver20Degree() ? "warm" : "cold";
        response.setOutput(
                String.format(
                        "%s has a rather %s day. And he has %d tnbs", request.getName(), tempFeeling, count));

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("faultCode", "200");
        jsonResponse.put("faultString", "Method success");
        jsonResponse.put("something", response);

        return jsonResponse;
    }

    @RpcMethod(name = "getTnbList")
    public Map<String, Object> getTnbList(TnbListRequest request) {
        TnbListResponse response = new TnbListResponse();
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            List<TnbDao> allTnbs = databaseConnection.getAllTnbs();
            String number = request.getNumber();

            if (request.getNumber().isEmpty()) {
                jsonResponse.put("faultCode", "404"); // is this the way to do that?
                jsonResponse.put("faultString", "Number not Found");
                return jsonResponse;
            }
            List<TnbDao> TnbsOfNumber = databaseConnection.getTnbsByNumber(number);
            TnbDao tnb = TnbsOfNumber.getFirst();

            List<TnbDao> tnbList = new ArrayList<>();
            TnbDao tnbToBeAdded = new TnbDao();
            tnbToBeAdded.setTnb("D001");
            tnbToBeAdded.setName("Deutsche Telekom");
            tnbToBeAdded.setIsTnb(tnb.getTnb().equals("D001"));

            tnbList.add(tnbToBeAdded);

            for (TnbDao tnbDao : allTnbs) {
                if (List.of("D146", "D218", "D248").contains(tnbDao.getTnb())) {
                    continue;
                }
                TnbDao tmpTnbToBeAdded = new TnbDao();
                tmpTnbToBeAdded.setTnb(tnbDao.getTnb());
                tmpTnbToBeAdded.setName(tnbDao.getName());
                tmpTnbToBeAdded.setIsTnb(true);
                tnbList.add(tmpTnbToBeAdded);
            }
            tnbList.sort(Comparator.comparing(TnbDao::getName));

            response.setTnb(tnbList);
        } catch (Exception e) {
            jsonResponse.put("faultCode", "500");
            jsonResponse.put("faultString", "Method failed");
            return jsonResponse;
        }

        jsonResponse.put("faultCode", "200");
        jsonResponse.put("faultString", "Method success");
        jsonResponse.put("payload", response);
        return jsonResponse;
    }
}
