package com.singlesoft.repaircon.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class servicesViewModel extends ViewModel {
    private MutableLiveData<List<Service>> dataList = new MutableLiveData<>();

    public void setDataList(List<Service> data) {
        dataList.setValue(data);
    }

    public LiveData<List<Service>> getDataList() {
        return dataList;
    }
}
