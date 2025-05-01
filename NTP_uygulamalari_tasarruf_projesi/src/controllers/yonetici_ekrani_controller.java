package controllers;

import application.*;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.projeMySql.util.VeritabaniUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

public class yonetici_ekrani_controller {
	public yonetici_ekrani_controller() {
		baglanti=VeritabaniUtil.Baglan();
	}
	
    @FXML
    private AnchorPane anchor_kimlik_arka;

    @FXML
    private AnchorPane anchor_kimlik_on;
	
	@FXML
    private AnchorPane scene_yonetici_ekrani;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_fatura_onayla;

    @FXML
    private Button btn_fatura_reddet;

    @FXML
    private Button btn_geri_don;

    @FXML
    private Button btn_kapat;

    @FXML
    private Button btn_uye_onayla;

    @FXML
    private Button btn_uye_sil;

    @FXML
    private TableColumn<faturalar, Date> column_fatura_tarih;

    @FXML
    private TableColumn<faturalar, Integer> column_fatura_id;

    @FXML
    private TableColumn<faturalar, Double> column_fatura_tutar;

    @FXML
    private TableColumn<uye_kayitlari, String> column_uye_onayla_ad_soyad;

    @FXML
    private TableColumn<uye_kayitlari, String> column_uye_onayla_tc;
    
    @FXML
    private TableColumn<uye_kayitlari, Date> column_uye_onayla_uyelik_tarihi;

    @FXML
    private TableColumn<uye_kayitlari, String> column_uye_sil_ad_soyad;

    @FXML
    private TableColumn<uye_kayitlari, Integer> column_uye_sil_hata_sayisi;

    @FXML
    private TableColumn<uye_kayitlari, String> column_uye_sil_tc;

    @FXML
    private ImageView img_kimlik_arka;

    @FXML
    private ImageView img_kimlik_on;

    @FXML
    private Hyperlink link_sifre_degistir;

    @FXML
    private Slider slider_kimlik_arka;

    @FXML
    private Slider slider_kimlik_on;

    @FXML
    private TableView<faturalar> tbl_fatura;

    @FXML
    private TableView<uye_kayitlari> tbl_uye_onayla;

    @FXML
    private TableView<uye_kayitlari> tbl_uye_sil;
    
    @FXML
    private Label lbl_yakinlastirma;
    
    Connection baglanti=null;
    PreparedStatement sorguIfadesi=null;
    ResultSet getirilen=null;
    String sql;
    String sql2;
    Integer giris_yapilan_id;
    yonetici_kayitlar yonetici_kayitlar;
	ObservableList<faturalar> fatura_gelen=FXCollections.observableArrayList();
	faturalar fatura;
	ObservableList<faturalar> selectedItems;
	ObservableList<uye_kayitlari> selectedItems2;
	ObservableList<uye_kayitlari> selectedItems3;
	ObservableList<uye_kayitlari> uyeler_gelen_onaylanmis=FXCollections.observableArrayList();
	ObservableList<uye_kayitlari> uyeler_gelen_onaylanmamis=FXCollections.observableArrayList();
	Integer count;

    @FXML
    void btn_fatura_onayla_click(ActionEvent event) {
        selectedItems = tbl_fatura.getSelectionModel().getSelectedItems();
        
        if (!selectedItems.isEmpty()) {
            sql="update faturalar set onay='onaylanmis' where uyeler_id=? and tarih=? and fatura_tipleri_id=(select id from fatura_tipleri where id=?)";
            try {
            	sorguIfadesi=baglanti.prepareStatement(sql);
            	sorguIfadesi.setInt(1, selectedItems.get(0).getUyeler_id());
            	sorguIfadesi.setDate(2, selectedItems.get(0).getTarih());
            	sorguIfadesi.setInt(3, selectedItems.get(0).getFatura_tipleri_id());
            	sorguIfadesi.executeUpdate();
                JOptionPane.showMessageDialog(new JFrame(), "Seçili fatura onaylandı", "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
	            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			}
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Tabloda seçili fatura yok.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
        tbl_fatura.getItems().clear();
        fatura_getir();
    }

    @FXML
    void btn_fatura_reddet_click(ActionEvent event) {
    	selectedItems = tbl_fatura.getSelectionModel().getSelectedItems();
        
        if (!selectedItems.isEmpty()) {
            sql="delete from faturalar where uyeler_id=? and tarih=? and fatura_tipleri_id=?";
            sql2="update uyeler set hatali_fatura_giris_sayisi=hatali_fatura_giris_sayisi+1 where id=?";
            try {
            	sorguIfadesi=baglanti.prepareStatement(sql);
            	sorguIfadesi.setInt(1, selectedItems.get(0).getUyeler_id());
            	sorguIfadesi.setDate(2, selectedItems.get(0).getTarih());
            	sorguIfadesi.setInt(3, selectedItems.get(0).getFatura_tipleri_id());
            	sorguIfadesi.executeUpdate();
            	sorguIfadesi=baglanti.prepareStatement(sql2);
            	sorguIfadesi.setInt(1, selectedItems.get(0).getUyeler_id());
            	sorguIfadesi.executeUpdate();
                JOptionPane.showMessageDialog(new JFrame(), "Seçili fatura reddedildi.", "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
	            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			}
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Tabloda seçili fatura yok.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
        tbl_fatura.getItems().clear();
        fatura_getir();
    }
    
	
    @FXML
    void btn_uye_engelle(ActionEvent event) {
    	selectedItems3 = tbl_uye_sil.getSelectionModel().getSelectedItems();
        
        if (!selectedItems3.isEmpty()) {
            sql="update uyeler set onay='engellenmis' where id=?";
            try {
            	sorguIfadesi=baglanti.prepareStatement(sql);
            	sorguIfadesi.setInt(1, selectedItems3.get(0).getId());
            	sorguIfadesi.executeUpdate();
                JOptionPane.showMessageDialog(new JFrame(), "Seçili üye engellendi.", "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
	            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			}
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Tabloda seçili üye yok.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
        tbl_fatura.getItems().clear();
        fatura_getir();
    }

    @FXML
    void btn_geri_don_click(ActionEvent event) {
    	try {
    		page_operations.page_switch("uye_girisi.fxml", event);
		} catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getMessage());
		}
    }

    @FXML
    void btn_kapat_click(ActionEvent event) {
    	System.exit(0);
    }

    @FXML
    void link_sifre_degistir_click(ActionEvent event) {
    	try {
    		page_operations.page_switch("yonetici_islem.fxml", event);
		} catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getMessage());
		}
    }
    
    @FXML
    void tbl_uye_onayla_mouse_clicked(MouseEvent event) throws SQLException {
    	image_zoom(img_kimlik_on, slider_kimlik_on);
    	image_zoom(img_kimlik_arka, slider_kimlik_arka);
    	
    	selectedItems2 = tbl_uye_onayla.getSelectionModel().getSelectedItems();
    	
    	blob_to_image(selectedItems2.get(0).getKimlik_foto1(), img_kimlik_on);
    	blob_to_image(selectedItems2.get(0).getKimlik_foto2(), img_kimlik_arka);
    }
    
    void blob_to_image(java.sql.Blob blob, ImageView imageView) throws SQLException {
    	byte[] blobBytes = blob.getBytes(1, (int) blob.length());

    	Image image = new Image(new ByteArrayInputStream(blobBytes));

    	imageView.setImage(image);
    }
    
    @FXML
    void btn_uye_onayla_click(ActionEvent event) {
    	selectedItems2.get(0).getId();
        
        if (!selectedItems2.isEmpty()) {
            sql="update uyeler set onay='onaylanmis', kimlik_foto1=null, kimlik_foto2=null where id=?";
            try {
            	sorguIfadesi=baglanti.prepareStatement(sql);
            	sorguIfadesi.setInt(1, selectedItems2.get(0).getId());
            	sorguIfadesi.executeUpdate();
                JOptionPane.showMessageDialog(new JFrame(), "Seçili üye onaylandı.", "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
	            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			}
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Tabloda seçili üye yok.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
        tbl_uye_onayla.getItems().clear();
        uye_getir("onaylanmamis");
    }
    
    @FXML
    void btn_uye_reddet_click(ActionEvent event) {
    	selectedItems2.get(0).getId();
        
        if (!selectedItems2.isEmpty()) {
            sql="delete from uyeler where id=?";
            try {
            	sorguIfadesi=baglanti.prepareStatement(sql);
            	sorguIfadesi.setInt(1, selectedItems2.get(0).getId());
            	sorguIfadesi.executeUpdate();
                JOptionPane.showMessageDialog(new JFrame(), "Seçili üye reddedildi.", "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
	            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			}
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Tabloda seçili üye yok.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
        tbl_uye_onayla.getItems().clear();
        uye_getir("onaylanmamis");
    }

    @FXML
    void initialize() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(anchor_kimlik_on.widthProperty());
        clip.heightProperty().bind(anchor_kimlik_on.heightProperty());
        anchor_kimlik_on.setClip(clip);

        Rectangle clip2 = new Rectangle();
        clip.widthProperty().bind(anchor_kimlik_arka.widthProperty());
        clip.heightProperty().bind(anchor_kimlik_arka.heightProperty());
        anchor_kimlik_arka.setClip(clip2);
        
        image_zoom(img_kimlik_on, slider_kimlik_on);
        image_zoom(img_kimlik_arka, slider_kimlik_arka);
        
    	
    	page_operations.upload_images_2button(btn_kapat, btn_geri_don);
    	
    	yonetici_kayitlar=yonetici_giris_controller.yonetici_kayitlar.get(0);
    	
    	fatura_getir();
    	
    	uye_getir("onaylanmis");
    	uye_getir("onaylanmamis");
    	
    	Tooltip tip=new Tooltip();
    	tip.setText("Kimlik fotoğraflarını yakınlaştırıp uzaklaştırmak için her iki taraftaki çubukları kullanabilirsiniz. "
    			+ "Aynı zamanda büyütülen fotoğrafı hareket ettirebilirsiniz.");
    	tip.setStyle("-fx-font-weight: bold");
    	lbl_yakinlastirma.setTooltip(tip);
    }
    
    void image_zoom(ImageView imageView, Slider zoomSlider) {
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double zoomFactor = newVal.doubleValue();
            imageView.setScaleX(zoomFactor);
            imageView.setScaleY(zoomFactor);
        });

        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();

        imageView.setOnMousePressed(event -> {
            mouseAnchor.set(new Point2D(event.getSceneX(), event.getSceneY()));
            imageView.getScene().setCursor(Cursor.MOVE);
        });

        imageView.setOnMouseReleased(event -> {
            imageView.getScene().setCursor(Cursor.DEFAULT);
        });

        imageView.setOnMouseDragged(event -> {
            Point2D dragPoint = new Point2D(event.getSceneX(), event.getSceneY());
            double deltaX = dragPoint.getX() - mouseAnchor.get().getX();
            double deltaY = dragPoint.getY() - mouseAnchor.get().getY();
            imageView.setTranslateX(imageView.getTranslateX() + deltaX);
            imageView.setTranslateY(imageView.getTranslateY() + deltaY);
            mouseAnchor.set(dragPoint);
        });

        imageView.setOnMouseEntered(event -> {
            imageView.getScene().setCursor(Cursor.HAND);
        });

        imageView.setOnMouseExited(event -> {
            imageView.getScene().setCursor(Cursor.DEFAULT);
        });
    }
    
    void fatura_getir() {
    	switch (yonetici_kayitlar.getFatura_tipleri_id()) {
		case 1: {
			sql="select * from faturalar where fatura_tipleri_id=1 and onay='onaylanmamis' and iller_id IN "
					+ "(select id from iller where elektrik_sirket_id=(select id from yoneticiler where id=?))";
			sql2="select tc from uyeler where id=?";
			break;
		}
		case 2: {
			sql="select * from faturalar where fatura_tipleri_id=2 and onay='onaylanmamis' and iller_id IN "
					+ "(select id from iller where su_sirket_id=(select id from yoneticiler where id=?))";
			sql2="select tc from uyeler where id=?";
			break;
		}
		case 3: {
			sql="select * from faturalar where fatura_tipleri_id=3 and onay='onaylanmamis' and iller_id IN "
					+ "(select id from iller where dogalgaz_sirket_id=(select id from yoneticiler where id=?))";
			sql2="select tc from uyeler where id=?";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: ");
		}
    	
    	tbl_fatura.setItems(fatura_gelen);
    	try {
    		count=0;
        	sorguIfadesi=baglanti.prepareStatement(sql);
        	sorguIfadesi.setInt(1, yonetici_kayitlar.getId());
        	ResultSet getirilen=sorguIfadesi.executeQuery();
        	while (getirilen.next()) {
        		fatura_gelen.add(new faturalar(getirilen.getDouble("tutar"),getirilen.getDate("tarih"), getirilen.getInt("uyeler_id"), 
        				getirilen.getInt("fatura_tipleri_id"), getirilen.getInt("ilceler_id"), getirilen.getInt("iller_id")));
        		count++;
			}
        	try {
        		sorguIfadesi=baglanti.prepareStatement(sql2);
            	for (Integer i=0; i<count; i++) {
            		sorguIfadesi.setInt(1, fatura_gelen.get(i).getUyeler_id());
                	ResultSet getirilen2=sorguIfadesi.executeQuery();
            		while (getirilen2.next()) {
                		fatura_gelen.get(i).setFatura_sahibi_tc(getirilen2.getString("tc"));
                	}
    			}
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
	            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
			}
        	
        	column_fatura_tutar.setCellValueFactory(new PropertyValueFactory<>("tutar"));
            column_fatura_tarih.setCellValueFactory(new PropertyValueFactory<>("tarih"));
            column_fatura_id.setCellValueFactory(new PropertyValueFactory<>("fatura_sahibi_tc"));
        	
            count=0;
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
            JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
		}
    }
    
    void uye_getir(String onay) {
    	switch (yonetici_kayitlar.getFatura_tipleri_id()) {
		case 1: {
			sql="select * from uyeler where onay='onaylanmis' and hatali_fatura_giris_sayisi>10 and iller_id IN(select id from iller where elektrik_sirket_id=?)";
			sql2="select * from uyeler where onay='onaylanmamis' and iller_id IN (select id from iller where elektrik_sirket_id=?)";
			break;
		}
		case 2: {
			sql="select * from uyeler where onay='onaylanmis' and hatali_fatura_giris_sayisi>10 and iller_id IN (select id from iller where su_sirket_id=?)";
			sql2="select * from uyeler where onay='onaylanmamis' and iller_id IN (select id from iller where su_sirket_id=?)";
			break;
		}
		case 3: {
			sql="select * from uyeler where onay='onaylanmis' and hatali_fatura_giris_sayisi>10 and iller_id IN (select id from iller where dogalgaz_sirket_id=?)";
			sql2="select * from uyeler where onay='onaylanmamis' and iller_id IN (select id from iller where dogalgaz_sirket_id=?)";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: ");
		}
    	if (onay=="onaylanmis") {
    		try {
    	    	sorguIfadesi=baglanti.prepareStatement(sql);
    	    	sorguIfadesi.setInt(1, yonetici_kayitlar.getId());
    			ResultSet getirilen = sorguIfadesi.executeQuery();
    			while (getirilen.next()) {
    				uyeler_gelen_onaylanmis.add(new uye_kayitlari(getirilen.getInt("id"), getirilen.getString("tc"), getirilen.getString("ad_soyad"),
    						getirilen.getDate("uyelik_tarihi"), getirilen.getBlob("kimlik_foto1"), getirilen.getBlob("kimlik_foto2"),
    						getirilen.getString("sifre"), getirilen.getInt("iller_Id"), getirilen.getInt("ilceler_Id"), 
    						getirilen.getInt("hatali_fatura_giris_sayisi"), getirilen.getString("onay")));
    			}
    			tbl_uye_onayla.setItems(uyeler_gelen_onaylanmamis);
    		    column_uye_onayla_tc.setCellValueFactory(new PropertyValueFactory<>("tc"));
    		    column_uye_onayla_ad_soyad.setCellValueFactory(new PropertyValueFactory<>("ad_soyad"));
    		    column_uye_onayla_uyelik_tarihi.setCellValueFactory(new PropertyValueFactory<>("uyelik_tarihi"));
    		} catch (Exception e) {
    			System.out.println(e.getMessage().toString());
    	        JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
    		}
		}
    	else if (onay=="onaylanmamis") {
    		try {
    	    	sorguIfadesi=baglanti.prepareStatement(sql2);
    	    	sorguIfadesi.setInt(1, yonetici_kayitlar.getId());
    			ResultSet getirilen = sorguIfadesi.executeQuery();
    			while (getirilen.next()) {
    				uyeler_gelen_onaylanmamis.add(new uye_kayitlari(getirilen.getInt("id"), getirilen.getString("tc"), getirilen.getString("ad_soyad"),
    						getirilen.getDate("uyelik_tarihi"), getirilen.getBlob("kimlik_foto1"), getirilen.getBlob("kimlik_foto2"),
    						getirilen.getString("sifre"), getirilen.getInt("iller_Id"), getirilen.getInt("ilceler_Id"), 
    						getirilen.getInt("hatali_fatura_giris_sayisi"), getirilen.getString("onay")));
    			}
    			tbl_uye_sil.setItems(uyeler_gelen_onaylanmis);
    		    column_uye_sil_tc.setCellValueFactory(new PropertyValueFactory<>("tc"));
    		    column_uye_sil_ad_soyad.setCellValueFactory(new PropertyValueFactory<>("ad_soyad"));
    		    column_uye_sil_hata_sayisi.setCellValueFactory(new PropertyValueFactory<>("hatalı_fatura_giris_sayisi"));
    		} catch (Exception e) {
    			System.out.println(e.getMessage().toString()+" 4");
    	        JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
    		}
		}
    	else {
	        JOptionPane.showMessageDialog(new JFrame(), "Beklenmedik bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
		}
    }

}
