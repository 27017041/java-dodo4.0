package com.embraiz.service;

import java.util.List;
import java.util.Map;

import com.embraiz.model.Payment;

public interface PaymentService {

	public String paymentNo();

	public int insert(Payment payment);

	public Map<String, Object> getPaymentDetail(Integer paymentId);

	public Map<String, Object> getDatasourse();

	public List<Payment> getpaymentById(String obj_id);

	public List<Map<String, Object>> getexportList(String searchForm);

}
