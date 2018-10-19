package com.apap.tugas1.service;

import java.util.List;
import com.apap.tugas1.model.JabatanModel;

public interface JabatanService {
	void tambahJabatan (JabatanModel jabatan);
	List <JabatanModel> selectAll ();
	JabatanModel findJabatanById (long id);
	void ubahJabatan (JabatanModel jabatan);
	void hapusJabatan (long id);
}
