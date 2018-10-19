package com.apap.tugas1.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apap.tugas1.model.ProvinsiModel;
import com.apap.tugas1.repository.ProvinsiDb;

@Service
@Transactional
public class ProvinsiServiceImp implements ProvinsiService{
	@Autowired
	ProvinsiDb provinsiDb;
	
	@Override
	public List<ProvinsiModel> selectAll() {
		// TODO Auto-generated method stub
		return provinsiDb.findAll();
	}

	@Override
	public ProvinsiModel findProvinsiById(long id) {
		// TODO Auto-generated method stub
		return provinsiDb.findProvinsiById(id);
	}
}
