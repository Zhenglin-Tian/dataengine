package com.tcredit.engine.processService;


import com.tcredit.engine.response.DataResponse;

import javax.servlet.http.HttpServletRequest;

public interface DataService {

    DataResponse getDataProd(HttpServletRequest request);

}
