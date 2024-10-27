package com.example.kiotz.repositories;

import com.example.kiotz.database.IDataBaseService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IRepository<T> extends IDataBaseService<T> {

}
