package com.chan.service;

import java.util.List;

/**
 * Created by keyez on 2017/12/6.
 */
public interface SearchRolesExecutor<T> {

    List<T> searchRoles(String group);
}
