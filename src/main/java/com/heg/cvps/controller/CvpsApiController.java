// package com.heg.cvps.controller;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.heg.cvps.entity.CvpsRequest;
// import com.heg.cvps.service.CvpsRequestService;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/v1/permissions")
// public class CvpsApiController {

//     private final CvpsRequestService service;

//     public CvpsApiController(CvpsRequestService service) {
//         this.service = service;
//     }

//     // 1. POST Endpoint - Submits and registers a brand new entry pass request [cite: 100, 352]
//     @PostMapping
//     public ResponseEntity<CvpsRequest> submitVehiclePermissionRequest(@Valid @RequestBody CvpsRequest incomingPayload) {
//         CvpsRequest registeredRecord = service.saveVehicleRequest(incomingPayload);
//         return new ResponseEntity<>(registeredRecord, HttpStatus.CREATED);
//     }

//     // 2. GET Endpoint - Fetches the verification records by parsing the vehicle number [cite: 127, 336]
//     @GetMapping("/{vehicleNo}")
//     public ResponseEntity<CvpsRequest> inspectVehiclePermissionStatus(@PathVariable String vehicleNo) {
//         CvpsRequest trackingDetails = service.getRequestByVehicleNo(vehicleNo);
//         return ResponseEntity.ok(trackingDetails);
//     }
// }



package com.heg.cvps.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.service.CvpsRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/permissions")
public class CvpsApiController {

    private final CvpsRequestService service;

    public CvpsApiController(CvpsRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CvpsRequest> submitVehiclePermissionRequest(@Valid @RequestBody CvpsRequest incomingPayload) {
        CvpsRequest savedRecord = service.saveVehicleRequest(incomingPayload);
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    @GetMapping("/{vehicleNo}")
    public ResponseEntity<CvpsRequest> inspectVehiclePermissionStatus(@PathVariable String vehicleNo) {
        CvpsRequest activeRecord = service.getRequestByVehicleNo(vehicleNo);
        return ResponseEntity.ok(activeRecord);
    }

    // 🆕 ADD THIS METHOD TO EXPOSE THE "GET ALL" ENDPOINT
    @GetMapping
    public ResponseEntity<List<CvpsRequest>> getAllPermissionRequests() {
        List<CvpsRequest> allRecords = service.getAllRequests();
        return ResponseEntity.ok(allRecords);
    }
}