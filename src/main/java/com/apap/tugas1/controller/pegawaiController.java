package com.apap.tugas1.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.apap.tugas1.model.InstansiModel;
import com.apap.tugas1.model.JabatanModel;
import com.apap.tugas1.model.JabatanPegawaiModel;
import com.apap.tugas1.model.PegawaiModel;
import com.apap.tugas1.model.ProvinsiModel;
import com.apap.tugas1.service.InstansiService;
import com.apap.tugas1.service.JabatanPegawaiService;
import com.apap.tugas1.service.JabatanService;
import com.apap.tugas1.service.PegawaiService;
import com.apap.tugas1.service.ProvinsiService;


@Controller
public class pegawaiController {
	@Autowired 
	PegawaiService pegawaiService;
	
	@Autowired
	InstansiService instansiService;
	
	@Autowired
	JabatanService jabatanService;
	
	@Autowired
	ProvinsiService provinsiService;
	
	@Autowired
	JabatanPegawaiService jabatanPegawaiService;

	
	//HOME
	@RequestMapping ("/")
	public String main (Model model) {
		List <JabatanModel> allJabatan = jabatanService.selectAll();
		List <InstansiModel> allInstansi = instansiService.selectAll();
		model.addAttribute("listJabatan", allJabatan);
		model.addAttribute("listInstansi", allInstansi);
		return "main";
	}
	
	
	
	//NO 1
	@RequestMapping ("/pegawai{nip}")
	public String tampilkan (String nip, Model model) {	
		PegawaiModel pegawai = pegawaiService.cariPegawaiByNip(nip);
		List <String> namaJabatanPegawai = new ArrayList<String>();
		double gajiTertinggi = 0;
		
		for (JabatanPegawaiModel jabatan : pegawai.getJabatanPegawai()) {
			namaJabatanPegawai.add(jabatan.getJabatan().getNama());
			if (jabatan.getJabatan().getGajiPokok() > gajiTertinggi) {
				gajiTertinggi = jabatan.getJabatan().getGajiPokok();
			}
		}
		double gaji = gajiTertinggi + (pegawai.getInstansi().getProvinsi().getPresentase_tunjangan() * gajiTertinggi/100);
		String kotaInstansi = pegawai.getInstansi().getProvinsi().getNama();
		int gajiInt = (int) gaji;
		model.addAttribute("listJabatan", namaJabatanPegawai);
		model.addAttribute("pegawai", pegawai);
		model.addAttribute("gaji", gajiInt);
		model.addAttribute("kota", kotaInstansi);
		return "pegawaiView";
	}
	
	@RequestMapping (value = "/pegawai/tambah")
	public String tambahPegawai (Model model) {
		List <ProvinsiModel> allProvinsi = provinsiService.selectAll();
		ProvinsiModel provPertama = allProvinsi.get(0);
		List <InstansiModel> allInstansiAwal = provPertama.getListInstansi();
		List <JabatanModel> allJabatan = jabatanService.selectAll();
		model.addAttribute("pegawai", new PegawaiModel());
		model.addAttribute("allProvinsi", allProvinsi);
		model.addAttribute("allInstansi",allInstansiAwal);
		model.addAttribute("allJabatan", allJabatan);
		return "formPegawai";
	}
	
	
	@RequestMapping (value = "/pegawai/cekInstansi", method = RequestMethod.GET)
	public @ResponseBody Object coba (@RequestParam ("id") String id, Model model) {
		List <InstansiModel> instansi = new ArrayList <InstansiModel>();
		if (id == null) {
			instansi = instansiService.selectAll();	
		}
		ProvinsiModel provinsi = provinsiService.findProvinsiById(Long.parseLong(id));
		instansi = provinsi.getListInstansi();
		model.addAttribute("allInstansi", instansi);
		Object inst = instansi;
		return inst;
	}
	
	@RequestMapping (value = "/pegawai/cekJabatan", method = RequestMethod.GET)
	public @ResponseBody Object jabatan (Model model) {
		List <JabatanModel> jabatan = jabatanService.selectAll();
		return jabatan;
	}
	
	@RequestMapping (value = "/pegawai/tambah", method = RequestMethod.POST)
	public String tambahPegawai (Model model, @ModelAttribute PegawaiModel pegawai) throws ParseException {
		for (JabatanPegawaiModel jab : pegawai.getJabatanPegawai()) {
			jab.setPegawai(pegawai);
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String tanggal = formatter.format(pegawai.getTanggallahir());
		java.util.Date formattedTglLahir =  formatter.parse(tanggal);
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String tanggalLahirFormat = format.format(formattedTglLahir);
		String tglLahir = tanggalLahirFormat.substring(0, 4) + ""+ tanggalLahirFormat.substring(6);
		
		
		String tanggalBaru ="" + pegawai.getTanggallahir() + "";
		InstansiModel instansi = instansiService.cariInstansiById(pegawai.getInstansi().getId());		
		List <PegawaiModel> allPegawai = instansi.getPegawai_instansi();
		int indexPegawai = 1;
		for (PegawaiModel peg : allPegawai) {
			String tgl = "" + peg.getTanggallahir() + "";
			if (peg.getTahun_masuk().equalsIgnoreCase(pegawai.getTahun_masuk()) && tgl.equalsIgnoreCase(tanggalBaru)) {
				indexPegawai++;
			}
		}
		
		String no_urut = "";
		if (indexPegawai < 10) {
			no_urut = "0" + indexPegawai;
		}
		else {
			no_urut = "" + indexPegawai;
		}
		
		String kodeInstansi = ""+pegawai.getInstansi().getId()+"";
		
		String nip = kodeInstansi + tglLahir + pegawai.getTahun_masuk() + no_urut;
		pegawai.setNip(nip);
		pegawaiService.addPegawai(pegawai);
		model.addAttribute("nips", nip);
		return "berhasilTambahPegawai";
	}
	
	
	
	//NO 10
	@RequestMapping (value = "pegawai/termuda-tertua", method = RequestMethod.GET)
	public String PegawaiTertuaTermuda (@RequestParam ("idInstansi") String id, Model model) {
		InstansiModel instansi = instansiService.cariInstansiById(Long.parseLong(id));
		List<PegawaiModel> pegUrut = pegawaiService.getPegawaiUrut(instansi);
		
		PegawaiModel pegTua = pegUrut.get(0);
		PegawaiModel pegMuda = pegUrut.get(pegUrut.size()-1);
		
		//Hitung Tua
		List <String> namaJabatanPegawaiTua = new ArrayList<String>();
		double gajiTertinggiTua = 0;
		for (JabatanPegawaiModel jabatan : pegTua.getJabatanPegawai()) {
			namaJabatanPegawaiTua.add(jabatan.getJabatan().getNama());
			if (jabatan.getJabatan().getGajiPokok() > gajiTertinggiTua) {
				gajiTertinggiTua = jabatan.getJabatan().getGajiPokok();
			}
		}
		double gajiTua = gajiTertinggiTua + (pegTua.getInstansi().getProvinsi().getPresentase_tunjangan() * gajiTertinggiTua/100);		
		
		//Hitung Muda
		List <String> namaJabatanMuda = new ArrayList<String>();
		double gajiTertinggiMuda = 0;
		
		for (JabatanPegawaiModel jabatan : pegMuda.getJabatanPegawai()) {
			namaJabatanMuda.add(jabatan.getJabatan().getNama());
			if (jabatan.getJabatan().getGajiPokok() > gajiTertinggiMuda) {
				gajiTertinggiMuda = jabatan.getJabatan().getGajiPokok();
			}
		}
		double gajiMuda = gajiTertinggiMuda + (pegMuda.getInstansi().getProvinsi().getPresentase_tunjangan() * gajiTertinggiMuda/100);
		
		model.addAttribute("pegTua", pegTua);
		model.addAttribute("pegMuda", pegMuda);
		model.addAttribute("listJabatanTua", namaJabatanPegawaiTua);
		model.addAttribute("gajiTua", gajiTua);
		model.addAttribute("gajiMuda", gajiMuda);
		model.addAttribute("listJabatanMuda", namaJabatanMuda);
		model.addAttribute("instansi", instansi);
		model.addAttribute("kotaInstansi", instansi.getProvinsi().getNama());
		return "lihatPegawaiTertua-Termuda";
	}
	

	
	//NO 4
	@RequestMapping (value = "pegawai/cari", method = RequestMethod.GET)
	public String cariPegawaiGet (@RequestParam (value = "idProvinsi", required = false) String idProvinsi,
								@RequestParam (value = "idInstansi", required = false) String idInstansi,
								@RequestParam (value = "idJabatan", required = false) String idJabatan,
								Model model) {
		
		List <ProvinsiModel> provinsi = provinsiService.selectAll();
		List <InstansiModel> instansi = instansiService.selectAll();
		List <JabatanModel> jabatan = jabatanService.selectAll();
		model.addAttribute("provinsi", provinsi);
		model.addAttribute("instansi", instansi);
		model.addAttribute("jabatan", jabatan);
		
		List <PegawaiModel> pegawai = new ArrayList<PegawaiModel>();
		
		if(!(idProvinsi ==null && idInstansi == null  && idJabatan == null)) {
			//1. isi provinsi aja
			if ((idProvinsi != "" && idInstansi == "" && idJabatan == "")) {
				ProvinsiModel prov = provinsiService.findProvinsiById(Long.parseLong(idProvinsi));
				List <InstansiModel> inst = prov.getListInstansi();
				for (InstansiModel i: inst) {
					for (PegawaiModel peg : i.getPegawai_instansi()) {
						pegawai.add(peg);
					}
				}
			}
			
			//2. instansi aja atau  3. provinsi dan instansi)
			else if ( (idProvinsi == "" && idInstansi != ""  && idJabatan =="") || (idProvinsi != "" && idInstansi != "" && idJabatan == "" )) {
				InstansiModel ins = instansiService.cariInstansiById(Long.parseLong(idInstansi));
				for (PegawaiModel p:ins.getPegawai_instansi()) {
					pegawai.add(p);
				}
			}
				
			//4. jabatan aja
			else if (idProvinsi =="" && idInstansi == "" && idJabatan != "") {
				JabatanModel jab = jabatanService.findJabatanById(Long.parseLong(idJabatan));
				List <JabatanPegawaiModel> jabPeg = jab.getJabatanPegawai();
				for (JabatanPegawaiModel jp: jabPeg) {
					pegawai.add(jp.getPegawai());
				}
			}
				
			//5. instansi dan jabatan
			else if(idProvinsi == "" && idInstansi != "" && idJabatan != "" ) {
				InstansiModel ins = instansiService.cariInstansiById(Long.parseLong(idInstansi));
				
				// ambil semua pegawai abis itu filter yang cocok jabatannya
				List <PegawaiModel> pegSementara = ins.getPegawai_instansi();
				for (PegawaiModel ps : pegSementara) {
					List<JabatanPegawaiModel> jabatanPegawaiPS = ps.getJabatanPegawai();
					for (JabatanPegawaiModel jb : jabatanPegawaiPS) {
						if(jb.getJabatan().getId() == Long.parseLong(idJabatan)) {
							pegawai.add(ps);
						}
							
					}
				}
			}
				
			//6. provinsi dan jabatan
			else if (idProvinsi != "" && idInstansi == "" && idJabatan != "") {
				ProvinsiModel prov = provinsiService.findProvinsiById(Long.parseLong(idProvinsi));
				List <InstansiModel> ins = prov.getListInstansi();
				List <PegawaiModel> allPeg = new ArrayList <PegawaiModel>();
				for (InstansiModel i : ins){
					List <PegawaiModel> pegawaiPerInstansi = i.getPegawai_instansi();
					for (PegawaiModel p:pegawaiPerInstansi) {
						allPeg.add(p);
					}
				}
				
				for (PegawaiModel pp: allPeg) {
					List <JabatanPegawaiModel> allJabatanPerPegawai = pp.getJabatanPegawai();
					for (JabatanPegawaiModel jpp : allJabatanPerPegawai) {
						if (jpp.getJabatan().getId() == Long.parseLong(idJabatan)) {
							pegawai.add(pp);
						}
					}
				}
			}
			
			//7. ketiga tiganya
			else {
				InstansiModel in = instansiService.cariInstansiById(Long.parseLong(idInstansi));
				List <PegawaiModel> pegSeInstansi = in.getPegawai_instansi();
				for (PegawaiModel ps : pegSeInstansi) {
					List <JabatanPegawaiModel> psJabatan = ps.getJabatanPegawai();
					for (JabatanPegawaiModel jbp : psJabatan) {
						if (jbp.getJabatan().getId() == Long.parseLong(idJabatan)) {
							pegawai.add(ps);
						}
					}
				}
			}
		
			model.addAttribute("pegawai", pegawai);
			return "cariPegawai";
		}
		
		else {
			model.addAttribute("pegawai", pegawai);
			return "cariPegawai";
		}
	}
	
	
	//NO 2
	@RequestMapping (value = "pegawai/ubah", method = RequestMethod.GET)
	public String ubahPegawai (@RequestParam (value = "nip") String nip, Model model) {
		PegawaiModel pegawai = pegawaiService.cariPegawaiByNip(nip);
		List <ProvinsiModel> allProvinsi = provinsiService.selectAll();
		ProvinsiModel provPertama = pegawai.getInstansi().getProvinsi();
		List <InstansiModel> allInstansiAwal = provPertama.getListInstansi();
		List <JabatanModel> allJabatan = jabatanService.selectAll();
		
		model.addAttribute("pegawai", pegawai);
		model.addAttribute("size", pegawai.getJabatanPegawai().size());
		model.addAttribute("allProvinsi", allProvinsi);
		model.addAttribute("allInstansi", allInstansiAwal);
		model.addAttribute("allJabatan", allJabatan);
		return "editPegawai";
	}
	
	@RequestMapping (value = "pegawai/ubah", method = RequestMethod.POST)
	public String simpanUbahPegawai (@ModelAttribute PegawaiModel pegawai, Model model) throws ParseException {
		PegawaiModel pegawaiLama = pegawaiService.cariPegawaiById(pegawai.getId());
		pegawaiService.hapusJabatanPegawaiPegawai(pegawaiLama);
		
		for (JabatanPegawaiModel pegjab : pegawai.getJabatanPegawai()) {
			pegjab.setPegawai(pegawai);
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String tanggal = formatter.format(pegawai.getTanggallahir());
		java.util.Date formattedTglLahir =  formatter.parse(tanggal);
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String tanggalLahirFormat = format.format(formattedTglLahir);
		
		String tglLahir = tanggalLahirFormat.substring(0, 4) + ""+ tanggalLahirFormat.substring(6); //ddmmyy
		String tanggalBaru ="" + pegawai.getTanggallahir() + "";
		
		InstansiModel instansi = instansiService.cariInstansiById(pegawai.getInstansi().getId());
		List <PegawaiModel> allPegawai = instansi.getPegawai_instansi();
		int indexPegawai = 1;
		for (PegawaiModel peg : allPegawai) {
			String tgl = "" + peg.getTanggallahir() + "";
			
			if (peg.getTahun_masuk().equalsIgnoreCase(pegawai.getTahun_masuk()) && tgl.equalsIgnoreCase(tanggalBaru)) {
				indexPegawai++;
			}
		}
		
		String no_urut = "";
		if (indexPegawai < 10) {
			no_urut = "0" + indexPegawai;
		}
		else {
			no_urut = "" + indexPegawai;
		}
		
		String kodeInstansi = ""+pegawai.getInstansi().getId()+"";
		
		String nip = kodeInstansi + tglLahir + pegawai.getTahun_masuk() + no_urut;
		
		pegawai.setNip(nip);
		pegawaiService.updatePegawai(pegawai, pegawai.getId());
		model.addAttribute("nips", nip);
		return "berhasilUbahPegawai";
	}
}
