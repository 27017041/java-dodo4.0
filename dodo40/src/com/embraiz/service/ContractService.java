package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.Contract;
import com.embraiz.model.ContractRenew;
import com.embraiz.model.User;

public interface ContractService {

	public int insert(Contract contract, User user);

	public String contractNo();

	public int deleteData(String ids, User user);

	public Map<String, Object> getAllstatus();

	public Map<String, Object> getQuotationByCompanyId(int companyId);

	public Map<String, Object> getAllType();

	public int saveRenew(ContractRenew contractRenew);

	public Map<String, Object> getlastNo(Contract contract);

	public int deleteRenew(String ids);

}
