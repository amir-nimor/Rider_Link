Scenario
You are building a Spring Boot REST API for RiderLink, a ride-sharing platform. The platform has two
types of accounts: Riders (passengers) and Drivers. Both share common account fields (username,
password, fullName, phone), but each has its own specific field (Rider has homeAddress; Driver has
vehicleLicenseNumber).
The API must support:
Public signup for both Riders and Drivers (with BCrypt-hashed passwords)
Database-backed login
A protected directory of all users (no passwords exposed)
A protected "me" endpoint that returns the logged-in user's own profile
A public health endpoint
Data storage: PostgreSQL
Learning objectives (mapped to sessions):
§4–§6 → Session 8/28 (@Table, @UniqueConstraint, @Column) + Session 8/29 (@Inheritance,
@DiscriminatorValue)
§7–§9 → Session 8/28 (@Entity, @Transactional, custom exception, PasswordEncoder) + Session
8/29 (JpaRepository, existsByUsername, save)
§10 → Session 8/29 (DatabaseUserDetailsService, loadUserByUsername)
§11 → Session 8/29 (SecurityFilterChain, csrf.disable(), permitAll, formLogin)
§12 → Session 8/29 (@Valid @RequestBody, BindingResult, ResponseEntity)
§13 → Conceptual understanding
§14–§16 → Session 8/29 (.http testing, JSESSIONID)
§17 → Research task assigned by the teacher
Assumed prior knowledge: Spring MVC basics (@RequestMapping, @GetMapping, @PathVariable,
@RequestParam) from weeks 20–30. All new patterns in this exercise are strictly from Sessions 8/28
and 8/29.

Project Setup Create a Spring Boot project with dependencies: Spring Web Spring Data JPA Spring Security Validation PostgreSQL Driver Lombok Package structure:
text com.riderlink ├── config ├── controller ├── dto ├── exception ├── model ├── repository └── service

Application Entry Point Create RiderLinkApplication in com.riderlink with @SpringBootApplication and a main method calling SpringApplication.run(...).


Configuration — application.properties Server: server.port=8090 (must NOT be 8080) 
PostgreSQL: jdbc:postgresql://localhost:5432/riderlinkdb,
user postgres,
your password, 
driver org.postgresql.Driver JPA: ddl-auto=update,
show-sql=true,
format_sql=true, 
dialect org.hibernate.dialect.PostgreSQLDialect 
Setup: CREATE DATABASE riderlinkdb; before running


Base Entity — UserDao Create UserDao in com.riderlink.model.
This is the base class for both Riders and Drivers.
Requirements:
@Entity @Table(name = "APP_USER", uniqueConstraints = @UniqueConstraint(name = "uk_app_user_username", columnNames = "username")) @Inheritance(strategy = InheritanceType.SINGLE_TABLE) 
Lombok: @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder

Fields: id: Long — @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
username: String — @Column(nullable = false, length = 20)
password: String — @Column(nullable = false) (BCrypt hash)
fullName: String — @Column(nullable = false, length = 100)
phone: String — @Column(nullable = false, length = 15)
Comment task (above the class, 2 sentences):
Why do we use @Table(uniqueConstraints=...) 
instead of @Column(unique=true) on username?
Comment task (above the class, 1–2 sentences):
Why is the table named APP_USER and not user? Answer from PostgreSQL's perspective.


Subclasses — Rider and Driver Create Rider in com.riderlink.model:
@Entity @DiscriminatorValue("RIDER") Lombok annotations (same as parent, except no @Builder conflict — omit if needed)
extends UserDao Extra field: homeAddress: String — @Column(length = 200) Create Driver in com.riderlink.model:
@Entity @DiscriminatorValue("DRIVER") Lombok annotations extends UserDao Extra field:
vehicleLicenseNumber: String — @Column(length = 30)
Important: The subclass-specific fields must be nullable — in SINGLE_TABLE strategy, a Rider's row will have vehicle_license_number = NULL, and vice-versa.

DTOs 6-1. RiderSignupRequest (record, package com.riderlink.dto) Components and validation:
username: @NotBlank @Size(min=5, max=20) @Pattern(regexp="[a-zA-Z0-9_]+") 
password: @NotBlank @Size(min=8, max=72) @Pattern(regexp="[\x20-\x7E]+", 
message="Password must contain printable ASCII characters") 7 
fullName: @NotBlank @Size(max=100)
phone: @NotBlank @Size(min=7, max=15)
homeAddress: @NotBlank @Size(max=200) 

6-2. DriverSignupRequest (record, package com.riderlink.dto)
Same as above, but the last field is vehicleLicenseNumber:
String @NotBlank @Size(max=30) instead of homeAddress. 

6-3. UserResponse (record, package com.riderlink.dto) Components: id, username, fullName, phone. Comment task
(above UserResponse, 1 sentence): Why do we use a separate response DTO instead of returning the entity?

Repositories All extend JpaRepository 
(the pattern from Session 8/29).
7-1. UserRepository extends JpaRepository<UserDao, Long> Optional<UserDao> findByUsername(String username) — derived query boolean existsByUsername(String username) — derived query 
7-2. RiderRepository extends JpaRepository<Rider, Long> 
7-3. DriverRepository extends JpaRepository<Driver, Long> Annotate each with @Repo



=========================================================


@Service
Constructor injection of UserRepository, RiderRepository, PasswordEncoder — fields final
One method signup(RiderSignupRequest request) annotated @Transactional:
If userRepository.existsByUsername(request.username()) → throw UsernameAlreadyExistsException
Build Rider with userRepository.save(...) or riderRepository.save(...) (either is fine — a Rider IS-A
UserDao)
Hash the password with passwordEncoder.encode(...)
Return nothing (void)
9-2. DriverSignupService — identical pattern for Drivers.
9-3. UserQueryService
@Service
Constructor injection of UserRepository
List<UserResponse> findAll() — maps entities to UserResponse, no passwords
UserResponse findByUsername(String username) — returns null if not found
10. DatabaseUserDetailsService
    Create in com.riderlink.service:
    @Service
    Implements UserDetailsService
    Constructor injection of UserRepository
    @Transactional @Override public UserDetails loadUserByUsername(String username):
    Find the user or throw UsernameNotFoundException
    Return User.withUsername(...).password(...).roles("USER").build()
    Do NOT compare passwords here — Spring Security compares via PasswordEncoder.matches(...)
    automatically.
11. SecurityConfig
    Create in com.riderlink.config:
    @Bean PasswordEncoder passwordEncoder() → new BCryptPasswordEncoder()
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http):
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/signup/rider", "/auth/signup/driver",
    "/health").permitAll().anyRequest().authenticated())
    .formLogin(Customizer.withDefaults())
    ⚠️ Do NOT use .httpBasic(...) — it was not taught. Use form login only.
12. Controllers
    12-1. RiderSignupController (package com.riderlink.controller)
    @RestController, @RequiredArgsConstructor
    @PostMapping("/auth/signup/rider")
    Signature: ResponseEntity<Map<String,String>> signup(@Valid @RequestBody RiderSignupRequest
    req, BindingResult validation)
    If validation.hasErrors() → ResponseEntity.badRequest().body(Map.of("message", "Check your
    signup details"))
    try: riderSignupService.signup(req) →
    ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Rider account created"))
    catch UsernameAlreadyExistsException →
    ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username already exists"))
    12-2. DriverSignupController — identical pattern, but for Drivers.
    12-3. AuthController
    @RestController @RequestMapping("/auth"), @RequiredArgsConstructor
    @GetMapping("/me") — UserResponse me(Authentication authentication) →
    userQueryService.findByUsername(authentication.getName())
    12-4. UserDirectoryController
    @RestController @RequestMapping("/users"), @RequiredArgsConstructor
    @GetMapping → List<UserResponse> findAll()
    12-5. HealthController
    @RestController @RequestMapping("/health")
    @GetMapping → returns "OK"
13. Comment Tasks (conceptual — in code)
    Add each as a Java comment above the relevant class:
    Above RiderSignupController: In 2 sentences, explain what BindingResult does and why @Valid
    alone is not enough to handle validation errors gracefully.
    Above DatabaseUserDetailsService: In 2 sentences, explain the difference between the default
    Spring Security user (spring.security.user.name) and Database Authentication.
    Above SecurityConfig: In 1 sentence each, explain what csrf().disable() does and why we enable it
    here.
    Above AuthController: In 2–3 sentences, explain the difference between Authentication and
    Authorization, using the teacher's gatekeeper analogy.
14. .http Testing File
    Create riderlink-api.http in the project root with these requests. Use the ### separator between
    requests. Rest Client in IntelliJ (which students already use) preserves cookies across requests in the
    same file — so after login, subsequent requests will automatically include JSESSIONID.
    http

1. Health (no auth)
   GET http://localhost:8090/health

2. Rider signup
   POST http://localhost:8090/auth/signup/rider
   Content-Type: application/json
   {
   "username": "rider1",
   "password": "Secure123!",
   "fullName": "Sara Ahmadi",
   "phone": "09121234567",
   "homeAddress": "Tehran, Valiasr St."
   }

3. Rider signup — duplicate username (expect 500 + UsernameAlreadyExistsException)
   POST http://localhost:8090/auth/signup/rider
   Content-Type: application/json
   {
   "username": "rider1",
   "password": "Secure123!",
   "fullName": "Sara Again",
   "phone": "09121234568",
   "homeAddress": "Tehran"
   }

4. Driver signup
   POST http://localhost:8090/auth/signup/driver
   Content-Type: application/json
   {
   "username": "driver1",
   "password": "Secure123!",
   "fullName": "Reza Karimi",
   "phone": "09127654321",
   "vehicleLicenseNumber": "ABC-1234"
   }

5. Login as rider1 (form login → saves JSESSIONID cookie)
   POST http://localhost:8090/login
   Content-Type: application/x-www-form-urlencoded
   username=rider1&password=Secure123!

6. Directory (with JSESSIONID — expect all users, no passwords)
   GET http://localhost:8090/users

7. Me (with JSESSIONID)
   GET http://localhost:8090/auth/me

8. Dir