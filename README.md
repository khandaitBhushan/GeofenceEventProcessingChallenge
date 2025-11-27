
# 🚖 Geofence Event Processing Service

A real-time location tracking and geofencing service built for taxi companies.  
This backend system processes continuous GPS updates from vehicles, determines when vehicles **enter or exit defined geographic zones**, maintains transition history, and provides APIs to query zone status.

This project demonstrates:
- Clean code architecture
- Robust REST API development
- Practical geofence detection algorithm
- Real-time processing capability

---

## 🧠 Problem Statement & Objective

The system receives live GPS events for vehicles and must:
1. **Accept location events** via HTTP endpoint
2. **Determine zone entry/exit** based on coordinates
3. **Provide zone status** for any vehicle
4. **Track and store transition history**

---

## 🚀 Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Backend Framework | Spring Boot | 3.3.4 |
| Language | Java | 17 |
| Database | PostgreSQL | 16 |
| Build Tool | Maven | 4.0 |
| Deployment | Render Cloud | Live Production |
| Logging | SLF4J & Spring Boot Logging |
| Mapping & Geo | Custom Polygon Geofence Algorithm |
| API Testing | Postman / Curl |

---

## ⚙️ Requirements

### **Prerequisites**
- Java 17+
- Maven 4.x
- PostgreSQL installed locally or hosted
- Internet connection (for deployment)

### **Environment Variables**
```env
DATABASE_URL=<postgres-url>
DATABASE_USERNAME=<db-user>
DATABASE_PASSWORD=<db-password>
PORT=8080
````

---

## 🧪 API Endpoints

### **1️⃣ Send Location Event**

Process live GPS event from a vehicle

| Method       | POST                                                           |
| ------------ | -------------------------------------------------------------- |
| URL          | `https://geofence-service.onrender.com/api/v1/location-events` |
| Content-Type | `application/json`                                             |

#### **Request Body**

```json
{
  "vehicleId": "taxi_001",
  "latitude": 40.758,
  "longitude": -73.985
}
```

#### **Success Response**

```json
{
  "success": true,
  "message": "Location event processed successfully",
  "timestamp": "2025-11-27T17:15:59.267030605"
}
```

#### **Failure Response**

```json
{
  "success": false,
  "message": "Invalid location data",
  "errorCode": "BAD_REQUEST"
}
```

---

### **2️⃣ Get Current Zone of Vehicle**

| Method | GET                                                                           |
| ------ | ----------------------------------------------------------------------------- |
| URL    | `https://geofence-service.onrender.com/api/v1/vehicles/taxi_001/current-zone` |

#### **Success Response**

```json
{
  "success": true,
  "message": "Current zone status retrieved",
  "data": {
    "vehicleId": "taxi_001",
    "currentZoneId": "TMS",
    "currentZoneName": "Times Square",
    "latitude": 40.758,
    "longitude": -73.985,
    "lastUpdate": "2025-11-27T17:16:00.000000",
    "status": "IN_ZONE"
  },
  "timestamp": "2025-11-27T17:16:50.43311636"
}
```

#### **If NO_ZONE**

```json
{
  "success": true,
  "message": "Current zone status retrieved",
  "data": {
    "vehicleId": "taxi_001",
    "currentZoneId": null,
    "currentZoneName": null,
    "latitude": 40.750,
    "longitude": -73.990,
    "status": "NO_ZONE"
  }
}
```

---

### **3️⃣ Get Vehicle Transition History**

| Method | GET                                                                                 |
| ------ | ----------------------------------------------------------------------------------- |
| URL    | `https://geofence-service.onrender.com/api/v1/vehicles/taxi_001/transition-history` |

#### **Success Response**

```json
{
  "success": true,
  "message": "Transition history retrieved",
  "data": [
    {
      "id": 1,
      "vehicleId": "taxi_001",
      "zoneId": "TMS",
      "transitionType": "ENTER",
      "timestamp": "2025-11-27T15:32:11.458502",
      "latitude": 40.758,
      "longitude": -73.985
    }
  ]
}
```

---

## 🔍 How It Works (Algorithm Explanation)

1. Vehicle sends live GPS coordinates
2. System checks if coordinates lie inside any defined zone
3. Compares with previous zone state
4. If zone changed → create a **transition event**
5. Update current zone table
6. Return processed result

### **Geofence Detection**

* Uses bounding area polygon checks
* Ray-casting algorithm for point-in-polygon
* Avoids heavy libraries for performance

### **Database Tables**

| Table       | Description                       |
| ----------- | --------------------------------- |
| vehicles    | Stores last known location + zone |
| geozones    | Predefined zones with coordinates |
| transitions | enter/exit event history          |

---

## 🏁 Setup Instructions

```bash
git clone https://github.com/your-repo/geofence-service.git
cd geofence-service
mvn clean install
mvn spring-boot:run
```

Application will start at:

```
http://localhost:8080
```

To test:

```bash
curl https://geofence-service.onrender.com/actuator/health
```

---

## 🎯 Assumptions

* Zones are fixed and defined manually
* GPS accuracy tolerance is acceptable up to ~10 meters
* Vehicle events always contain valid coordinates
* Vehicle IDs are supplied externally

---

## 🧠 Design Decisions

| Choice                | Reason                                     |
| --------------------- | ------------------------------------------ |
| SQL DB over NoSQL     | Requires consistent historical tracking    |
| Real-time REST events | Easier integration with GPS devices        |
| Custom geofence logic | Performance and control                    |
| Transition tracking   | Useful for analytics, billing & compliance |

---

## 📈 Future Improvements / Next Roadmap

* Add WebSocket live vehicle map visualization
* Mobile app support
* Admin UI for geofence zone editing
* Kafka event streaming for high scale
* AI-based movement predictions
* Alert notifications (SMS/Email/WebPush)

---

## 👨‍💻 Author

**Bhushan Khandait**
Backend Engineer | Java | Spring Boot | REST APIs | PostgreSQL

---

### ⭐ If you like this project, give it a star on GitHub 🙌

