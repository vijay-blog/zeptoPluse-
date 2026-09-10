package com.nexamart.backend.security;
import org.springframework.security.core.Authentication;import org.springframework.security.core.context.SecurityContextHolder;
public final class CurrentUser{private CurrentUser(){} public static Long id(){Authentication a=SecurityContextHolder.getContext().getAuthentication();return a==null?null:Long.valueOf(a.getName());}}
