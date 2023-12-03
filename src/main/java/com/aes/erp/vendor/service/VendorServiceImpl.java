package com.aes.erp.vendor.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserService;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorFile;
import com.aes.erp.vendor.entity.VendorItem;
import com.aes.erp.vendor.enums.VendorDocType;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.repository.VendorFileRepository;
import com.aes.erp.vendor.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VendorProfileService vendorProfileService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserService userService;

    @Autowired
    private VendorFileRepository vendorFileRepository;

    @Override
    public Optional<?> getVendorDetail(Long vendorId) {
        Optional<?> vendorOptional = vendorRepository.findVendorById(vendorId);
        return vendorOptional;
    }

    @Override
    @Transactional
    public void createVendor(VendorDto vendorDto) {

//        if(vendorDto.getVendorType()==null || vendorDto.getVendorType().getId()== null){
//            throw new AesException("Vendor Type Required");
//        }

        if(!vendorDto.getPhone().isEmpty()){
            if(vendorDto.getPhone().matches("[a-zA-Z]")){
                throw new AesException("phone number should not contain alphabets");
            }
        }

        User user = userService.createVendorUserAccount(vendorDto);

        Vendor vendor = vendorDto.getEntity();
        vendor.setStatus(VendorStatus.CREATED);
        vendor.setCategory(new ItemCategory(vendorDto.getCategory().getId()));
        vendor.setSubCategory(new ItemCategory(vendorDto.getSubCategory().getId()));
//        vendor.setVendorItems(
//                vendorDto.getItems()
//                        .stream()
//                        .map(
//                                (item) -> new VendorItem(new Item(item.getId()), vendor)
//                        )
//                        .collect(Collectors.toList())
//        );
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.PENDING_DOCUMENT_VERIFICATION);
        vendor.setVendorType(vendorDto.getVendorType());
        vendor.setUser(user);
        vendor.setStartedAt(new Date());
        vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public void updateVendor(Long id, VendorDto vendorDto) {

        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Vendor Information not found");
        }

        Vendor vendor = vendorOptional.get();
        if(vendorDto.getName()!=null && !vendorDto.getName().isEmpty()) {
            vendor.setName(vendorDto.getName());
        }
        if(vendorDto.getEmail()!=null && !vendorDto.getEmail().isEmpty()) {
            vendor.setEmail(vendorDto.getEmail());
        }
        if(vendorDto.getPhone()!=null && !vendorDto.getPhone().isEmpty()) {
            vendor.setPhone(vendorDto.getPhone());
        }

    }

    @Override
    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }

    @Override
    public Page<?> getVendors(Optional<Integer> page, Optional<Integer> size) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        return vendorRepository.findAllVendors(pageable);
    }

    @Override
    public void uploadVendorFiles(Long id,
                                  String address,
                                  String password,
                                  String confirmPassword,
                                  Optional<MultipartFile> tinCert,
                                  Optional<MultipartFile> vatCert,
                                  Optional<MultipartFile> tradeLicense,
                                  Optional<MultipartFile> quotationFormat
    ) {

        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Vendor not found");
        }

        Path tinCertPath = Path.of("./uploads/vendor/"+id+"/tin-certificate");
        Path vatCertPath = Path.of("./uploads/vendor/"+id+"/vat-certificate");
        Path tradeLicensePath = Path.of("./uploads/vendor/"+id+"/trade-license");
        Path quotationFormatPath = Path.of("./uploads/vendor/"+id+"/quotation-format");

        List<VendorFile> vendorFiles = new ArrayList<>();
        Vendor vendor = vendorOptional.get();
        if(tinCert.isPresent()) {
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(tinCertPath, tinCert.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.TIN_CERTIFICATE);
                vendorFiles.add(vendorFile);
            }
        }

        if(vatCert.isPresent()){
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(vatCertPath, vatCert.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.VAT_CERTIFICATE);
                vendorFiles.add(vendorFile);
            }
        }

        if(tradeLicense.isPresent()){
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(tradeLicensePath, tradeLicense.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.TRADE_LICENSE);
                vendorFiles.add(vendorFile);
            }
        }

        if(quotationFormat.isPresent()){
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(quotationFormatPath, quotationFormat.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.QUOTATION_FORMAT);
                vendorFiles.add(vendorFile);
            }
        }


        vendorFileRepository.saveAll(vendorFiles);


    }

    @Override
    @Transactional
    public void updateVendorStatus(Long id, VendorStatus status) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Sorry! Vendor is not found");
        }
        Vendor vendor = vendorOptional.get();
        vendor.getUser().getUserCredential().setActive(
                (status==VendorStatus.DISABLED)? false : true
        );
        vendor.setStatus(status);

    }
    @Override
    public VendorProfileDto getVendorProfile(Long id){
        Vendor vendor = vendorRepository.findVendorByUserId(id);
        VendorProfileDto profileDto = new VendorProfileDto();
        profileDto.setBasicInformation(vendorProfileService.getVendorBasicInformation(vendor));
        profileDto.setIdentification(vendorProfileService.getVendorIdentification(vendor));
        profileDto.setAddress(vendorProfileService.getVendorAddress(vendor));
        profileDto.setName(vendor.getName());
        profileDto.setStartedAt(vendor.getStartedAt());
        return profileDto;
    }

    @Override
    public void approveVendor(Long vendorId) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(vendorId);
        if(!vendorOptional.isPresent())throw new AesException("Vendor not found");
        Vendor vendor = vendorOptional.get();
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.VERIFIED);
        vendor.setStatus(VendorStatus.ENABLED);
        vendorRepository.save(vendor);
    }
}
