CREATE DATABASE IF NOT EXISTS greengrocer;
USE greengrocer;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. İlk 3 kullanıcı (id 1, 2, 3) HARİÇ diğerlerini temizle
DELETE FROM userinfo WHERE id > 3;

-- 2. Yeni 25 Kullanıcı Ekle (Şifrelerin hepsi 'cust')
INSERT INTO userinfo (username, password, role, address, phone) VALUES 
('ahmet.yilmaz', 'cust', 'customer', 'Moda', '5552000001'),
('ayse.demir', 'cust', 'customer', 'Beşiktaş', '5552000002'),
('mehmet.kaya', 'cust', 'customer', 'Kuzguncuk', '5552000003'),
('fatma.celik', 'cust', 'customer', 'Nişantaşı', '5552000004'),
('mustafa.sahin', 'cust', 'customer', 'Balat', '5552000005'),
('zeynep.yildiz', 'cust', 'customer', 'Emirgan', '5552000006'),
('emre.aydin', 'cust', 'customer', 'Caddebostan', '5552000007'),
('elif.ozdemir', 'cust', 'customer', 'Florya', '5552000008'),
('burak.arslan', 'cust', 'customer', 'Ataşehir', '5552000009'),
('seda.polat', 'cust', 'customer', 'Kavacık', '5552000010'),
('can.kurt', 'cust', 'customer', 'Çekmeköy', '5552000011'),
('gamze.koc', 'cust', 'customer', 'Kartal', '5552000012'),
('volkan.tas', 'cust', 'customer', 'Kurtköy', '5552000013'),
('ozge.bulut', 'cust', 'customer', 'Cihangir', '5552000014'),
('hakan.sen', 'cust', 'customer', 'Etiler', '5552000015'),
('pinar.aksoy', 'cust', 'customer', 'Bebek', '5552000016'),
('tolga.yavuz', 'cust', 'customer', 'Tarabya', '5552000017'),
('merve.gungor', 'cust', 'customer', 'Bakırköy', '5552000018'),
('onur.kilic', 'cust', 'customer', 'Bahçelievler', '5552000019'),
('esra.dogan', 'cust', 'customer', 'Avcılar', '5552000020'),
('serkan.mutlu', 'cust', 'customer', 'Yeşilköy', '5552000021'),
('sude.sevim', 'cust', 'customer', 'Beylikdüzü', '5552000022'),
('irem.eroglu', 'cust', 'customer', 'Başakşehir', '5552000023'),
('selin.tekin', 'cust', 'customer', 'Göktürk', '5552000024'),
('baris.uzun', 'cust', 'customer', 'Silivri', '5552000025');

SET FOREIGN_KEY_CHECKS = 1;

-- Önce kontrol edin:
SHOW COLUMNS FROM Product LIKE 'discount_percent';

-- Eğer sonuç boşsa, ekleyin:

SELECT name, price, discount_percent FROM Product WHERE discount_percent > 0;