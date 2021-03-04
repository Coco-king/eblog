package top.codecrab.eblog.service;

import top.codecrab.eblog.common.response.Result;

public interface AdminService {
    Result jieSet(Long id, String field, Integer rank);
}
