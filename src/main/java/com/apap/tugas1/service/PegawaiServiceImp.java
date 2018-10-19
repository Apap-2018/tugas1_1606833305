package com.apap.tugas1.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apap.tugas1.model.InstansiModel;
import com.apap.tugas1.model.JabatanPegawaiModel;
import com.apap.tugas1.model.PegawaiModel;
import com.apap.tugas1.repository.PegawaiDb;

@Service
@Transactional
public class PegawaiServiceImp implements PegawaiService{
	@Autowired 
	private PegawaiDb pegawaiDb;

	@Autowired
	private JabatanPegawaiService jabatanPegawaiService;
	
	public PegawaiModel cariPegawaiByNip(String nip) {
		return pegawaiDb.findPegawaiByNip(nip);
	}

	@Override
	public void addPegawai(PegawaiModel pegawai) {
		pegawaiDb.save(pegawai);
	}

	@Override
	public List<PegawaiModel> selectAllPegawai() {
		// TODO Auto-generated method stub
		return pegawaiDb.findAll();
	}

	@Override
	public List<PegawaiModel> getPegawaiUrut(InstansiModel instansi) {
		// TODO Auto-generated method stub
		return pegawaiDb.findByInstansiOrderByTanggallahirAsc(instansi);
	}

	@Override
	public void deletePegawaiByNip(String nip) {
		PegawaiModel pegawai = pegawaiDb.findPegawaiByNip(nip);
		pegawaiDb.delete(pegawai);	
	}

	@Override
	public PegawaiModel cariPegawaiById(long id) {
		// TODO Auto-generated method stub
		return pegawaiDb.findPegawaiById(id);
	}

	@Override
	public void updatePegawai(PegawaiModel pegawaiBaru, long id) {
		// TODO Auto-generated method stub
		PegawaiModel pegawaiLama = pegawaiDb.findPegawaiById(id);
		pegawaiLama.setInstansi(pegawaiBaru.getInstansi());
		pegawaiLama.setJabatanPegawai(pegawaiBaru.getJabatanPegawai());
		pegawaiLama.setNama(pegawaiBaru.getNama());
		pegawaiLama.setNip(pegawaiBaru.getNip());
		pegawaiLama.setTahun_masuk(pegawaiBaru.getTahun_masuk());
		pegawaiLama.setTanggallahir(pegawaiBaru.getTanggallahir());
		pegawaiLama.setTempat_lahir(pegawaiBaru.getTempat_lahir());
		pegawaiDb.save(pegawaiLama);
	}

	@Override
	public void hapusJabatanPegawaiPegawai(PegawaiModel pegawai) {
		List <JabatanPegawaiModel> listJabatan = jabatanPegawaiService.findJabatanPegawaiByPegawai(pegawai);
		for (JabatanPegawaiModel jab : listJabatan) {
			jabatanPegawaiService.hapusJabatanPegawai(jab);
		}				
	}








	






	




}
