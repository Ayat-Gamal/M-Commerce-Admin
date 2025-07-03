# M-Commerce Admin App

> **Related Project:**  
> Check out the [Client App version](https://github.com/Ayat-Gamal/M-Commerce-App/tree/dev) for the customer-facing experience.

A powerful Android admin application built to manage a Shopify-based mobile commerce platform. Designed with scalability and simplicity in mind, this app enables store managers to create, edit, and control their store inventory, coupons, and product publishing directly from a mobile interface.

This project complements the Client App and communicates with Shopify's Admin API using both REST and GraphQL, following Clean Architecture and MVVM best practices.

---

## Features

- Add, edit, delete, and publish products with variants and images  
- Filter and search products by status (active, draft, archived)  
- Manage coupons: create, update, delete, and filter by type/status  
- View and adjust inventory levels  
- Live search and filtering for inventory and products  
- Built with Clean Architecture and MVVM  
- Uses both Shopify Admin REST and GraphQL APIs  

---

## Tech Stack
* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Architecture:** MVVM + Clean Architecture
* **Networking:** Retrofit (REST), Apollo GraphQL  
* **State Management:** StateFlow, ViewModel
* **Dependency Injection:** Hilt  
---
### Core
- `androidx.core:core-ktx`
- `androidx.appcompat:appcompat`
- `androidx.lifecycle:lifecycle-viewmodel-ktx`

### Dependency Injection
- `Hilt`

### Networking
- `Retrofit`
- `Apollo`
- `Apollo GraphQL`
- `OkHttp`

### Image Upload
- `Image Picker`
- `Multipart Upload` (OkHttp/Retrofit)

---

## Installation & Getting Started

### Prerequisites

- Android Studio Giraffe or newer  
- Kotlin 1.9+  
- Gradle 8+  
- JDK 17  
- Shopify Partner Account
---

## Libraries Used

### Core & Compose
* androidx.core: core-ktx
* androidx.activity: activity-compose
* androidx.compose: UI, Material3, Foundation, Icons

### Navigation & Architecture
* androidx.navigation: navigation-compose
* androidx.hilt: hilt-navigation-compose
* androidx.lifecycle: lifecycle-runtime-ktx, viewmodel-compose
* Hilt for DI

### Networking & Persistence
* Retrofit 
* DataStore

### UI & Media
* Coil for image loading
* Lottie Compose for animations

### Testing
* JUnit
* Mockk

---
###  Steps

```bash
# Clone the repo
git clone https://github.com/YourUsername/Admin-M-Commerce-App.git
cd Admin-M-Commerce-App

# Open in Android Studio and run
```

---
## Team Members

### Ahmed Mohamed Saad

**Mobile Software Engineer**
[GitHub](https://github.com/ahmedsaad207) | [LinkedIn](https://www.linkedin.com/in/dev-ahmed-saad/)

---

### Ayat Gamal Mustafa

**Mobile Software Engineer**
[GitHub](https://github.com/ahmedsaad207) | [LinkedIn](https://www.linkedin.com/in/ayat-gamal-700946229/)

---

### Mohamed Tag El-Deen Ahmed

**Mobile Software Engineer**
[LinkedIn](https://www.linkedin.com/in/mohamed-tag-eldeen)

---

### Youssif Nasser Mostafa

**Mobile Software Engineer**
[GitHub](https://github.com/JoeTP) | [LinkedIn](https://www.linkedin.com/in/youssif-nasser/)

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact

**Maintainer:** Ayat Gamal
[GitHub Profile](https://github.com/Ayat-Gamal)
Feel free to open issues or discussions for questions and ideas.

     
