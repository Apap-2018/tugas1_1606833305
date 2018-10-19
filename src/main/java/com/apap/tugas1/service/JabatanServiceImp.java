package com.apap.tugas1.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apap.tugas1.model.JabatanModel;
import com.apap.tugas1.repository.JabatanDb;

@Service
@Transactional
public class JabatanServiceImp implements JabatanService{
	
	@Autowired
	private JabatanDb jabatanDb;
	
	@Override
	public void tambahJabatan(JabatanModel jabatan) {
		jabatanDb.save(jabatan);
	}

	@Override
	public List<JabatanModel> selectAll() {
		// TODO Auto-generated method stub
		return jabatanDb.findAll();
	}

	@Override
	public JabatanModel findJabatanById(long id) {
		// TODO Auto-generated method stub
		
		return jabatanDb.findJabatanById(id);
	}

	@Override
	public void ubahJabatan(JabatanModel jabatan) {
		// TODO Auto-generated method stub
		JabatanModel oldJabatan = jabatanDb.findJabatanById(jabatan.getId());
		oldJabatan.setNama(jabatan.getNama());
		oldJabatan.setDeskripsi(jabatan.getDeskripsi());
		oldJabatan.setGajiPokok(jabatan.getGajiPokok());
		jabatanDb.save(jabatan);
	}

	@Override
	public void hapusJabatan(long id) {
		jabatanDb.deleteById(id);	
	}	
}