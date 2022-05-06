# CSE 241 Final Project

## Running the project
The mtc223 directory contains all java code and bin contains the jars and manifest file. The main jar executable is ui.jar, and it can be run simply by the shell script
```
sh run.sh
```
The build shell script recompiles all the files, and can be run using
```
sh build.sh
```

## Project specific details
In this project there are 3 basic menus, Property Manager, Tenant, and NUMA manager.

When you step into property manager and tenant, you will be prompted for a specific ID related to that menu. For the property manager, you can only manage apartments that are in the property ID you choose, and tenants can only see their information. NUMA doesn't have an associated property or apartment.

There can be multiple people on a lease, but payments to the lease effect the balance for each person on the lease. One person can have multiple leases.

Example:
- From the main menu
- (0) Go to property manager with PID 1
- (3) View lease data

Both tenants 1101 and 1102 are sharing a lease on apartment 12 with equal balance
- (6) Go back to main menu
- (1) Go to tenant manager with TID 1101
- (1) Make payment to lease with AID 12
- (5) Go back to main menu
- (0) Go to property manager with PID 1 again
- (3) View lease data

The balance was updated for everyone on the lease

Adding tenants to a lease must be done by tenants already on the lease

All data generation was done by hand, and is unique to my project.