package com.apap.tugas1.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apap.tugas1.model.InstansiModel;
import com.apap.tugas1.repository.InstansiDb;

@Service
@Transactional
public class InstansiServiceImp implements InstansiService{
	@Autowired
	private InstansiDb instansiDb;

	@Override
	public InstansiModel cariInstansiById(long id_instansi) {
		// TODO Auto-generated method stub
		return instansiDb.findInstansiById(id_instansi);
	}

	@Override
	public List<InstansiModel> selectAll() {
		// TODO Auto-generated method stub
		return instansiDb.findAll();
	}	
}
