package com.josephus.e_commerce_backend_app.common.interfaces;

import com.josephus.e_commerce_backend_app.common.domains.IamUserDetails;

public interface IamUserDetailsService {
    IamUserDetails findUserByUsername(String username);
}

