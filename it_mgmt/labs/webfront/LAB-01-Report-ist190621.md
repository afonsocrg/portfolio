# Webfront Lab README

AGISIT 20201-2022

## Authors

[//]: # (fill the following line with the Group Identifier, for example 03A or 12T, and then delete THIS line)
**Team 20A**

[//]: # (use photos of team members 150px height, square; and then delete THIS line)
<p align=center>
    <img src="../../doc/img/ist189399.png">
    <img src="../../doc/img/ist190621.png">
    <img src="../../doc/img/ist189498.png">
</p>


[//]: # (fill the following table with identifiers of each team member; and then delete THIS line)

| Number | Name              | Username                                     | Email                               |
| -------|-------------------|----------------------------------------------| ------------------------------------|
| ist189399 | Afonso Goncalves | <https://git.rnl.tecnico.ulisboa.pt/ist189399> | <mailto:afonso.corte-real.goncalves@tecnico.ulisboa.pt> |
| ist190621 | Maria Filipe | <https://git.rnl.tecnico.ulisboa.pt/ist190621> | <mailto:maria.j.d.c.filipe@tecnico.ulisboa.pt> |
| ist189498 | Maria Martins | <https://git.rnl.tecnico.ulisboa.pt/ist189498> | <mailto:maria.d.martins@tecnico.ulisboa.pt> |



## Q01
> *Interpret the Vagranfilethat will be used, explaining, in your own words, what you think the “instructions” in it are supposed to do.*

| Line no | Interpretation |
| ------- | -------------- |
| 5 | Assuring that the Vagrant provider is be VirtualBox |
| 7 - 16 | Installing the `vagrant-vbguest` and `vagrant-reload plugins` |
| 20 | Stops Vagrant from adding an ssh keypair to the guest |
| 21 | Vagrant updates GuestAdditions at each start |
| 22 | Stops Vagrant from checking for updates on `vagrant up` |
| 25, 54, 84 | Defines a VM in a multi-VM environment |
| 26, 55, 85 | Configures the OS that will run in the specified box |
| 27, 56, 86 | Sets the hostname the machine should have |
| 28, 57, 87 | Configures the network on the machine |
| 31, 61, 91 | Sets the machine name (the one that appears in the VirtualBox GUI |
| 32, 62, 92 | Lets guest machine to use the host's NAT and DNS resolver mechanisms |
| 33, 63, 93 | Sets the machine memory (MB) |
| 34, 64, 94 | Sets the number of virtual CPUs for the machine |
| 37 - 45, 67 - 75, 97 - 105 | Mounts the host `./tools` directory into the guest `/home/vagrant/tools` directory |
| 47 - 50, 77-78, 107-108 | Provisions the machines with configuration scripts. Reloads machine at the end to apply changes |
| 58 | Forwards host 8080 port to guest 80 |
| 82 | Repeats lines 83 - 109 3 times, replacing `#{i}` by the number of that iteration |


## Q02
> *Analyze briefly the `bootstrap.sh` the `host\_ip.sh` and the `host\_ssh.sh` to interpret their purpose.*

| File | Purpose |
| ------- | -------------- |
| `bootstrap.sh` | Scripts that install the required software for the VM to run (software-properties-common unzip build-essential libssl-dev libffi-dev gnupg python3-dev python3-pip ansible) |
| `hosts_ip.sh` | Maps IPs to each VM hostname|
| `host_ssh.sh` | Sets PasswordAuth to `yes`  |


## Q03
> *In what differs calling `ansible --version` from any directory, when compared by calling it from the Project directory you will launch?*

We noticed that the config file path changed in different directories.



## Q04
> *After changing the `Vagrantfile` to accommodate it for launching more web servers, which were the modifications in the files of the Project that you have done? (just tell the name of the file and the lines that were changed)*

Changed line 82  in vagrantfile in order to run more machines, uncommented the # sign in the tools/inventory.ini (line 9, 20, 28).


## Q05
> *Write the result of the command `ansible all -m shell -a "uptime"` for your modified infrastructure.*

localhost | CHANGED | rc=0 >>
 11:10:50 up 4 min,  1 user,  load average: 0.15, 0.31, 0.16
web3 | CHANGED | rc=0 >>
 11:10:51 up 2 min,  1 user,  load average: 0.31, 0.22, 0.09
web1 | CHANGED | rc=0 >>
 11:10:51 up 3 min,  1 user,  load average: 0.11, 0.21, 0.09
balancer | CHANGED | rc=0 >>
 11:10:54 up 3 min,  1 user,  load average: 0.14, 0.33, 0.16
web2 | CHANGED | rc=0 >>
 11:10:52 up 2 min,  1 user,  load average: 0.42, 0.56, 0.25



## Q06
> *Write the result of the command `ansible all -m shell -a "uname -a"` for your modified infrastructure*

localhost | CHANGED | rc=0 >>
Linux mgmt 5.4.0-88-generic #99-Ubuntu SMP Thu Sep 23 17:29:00 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux
web2 | CHANGED | rc=0 >>
Linux web2 5.4.0-88-generic #99-Ubuntu SMP Thu Sep 23 17:29:00 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux
web1 | CHANGED | rc=0 >>
Linux web1 5.4.0-88-generic #99-Ubuntu SMP Thu Sep 23 17:29:00 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux
balancer | CHANGED | rc=0 >>
Linux balancer 5.4.0-88-generic #99-Ubuntu SMP Thu Sep 23 17:29:00 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux
web3 | CHANGED | rc=0 >>
Linux web3 5.4.0-88-generic #99-Ubuntu SMP Thu Sep 23 17:29:00 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux


## Q07
> *When deploying the Network Time Protocol (NTP) you have changed the Reference Time Servers, by modifying the Playbook in order to include a "variable" to be replaced in the NTP configuration file when Ansible runs that Playbook. However, there was a "bug" inadvertently written for this new procedure. Did you find the Bug? What corrections were made, for the NTP service to run?*

The variable noc_ntpserver had the value server 0.europe.pool.ntp.org, which would generate the following line in the config file: server server 0.europe.pool.ntp.org, making it duplicate in the final configuration file. By changing the variable to 0.europe.pool.ntp.org only, the line would be corrected.


## Q08
> *You ran the `site_(docker/vbox).yml` Playbook. After running it for the second time, in case there were nor errors the would prevent all tasks to complete, can you describe: What happened? What did you find different (or not)?*

PLAY RECAP ***************************************************************************************************************
balancer                   : ok=9    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
web1                       : ok=7    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
web2                       : ok=7    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
web3                       : ok=7    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   

Meaning nothing changed.

## Q09
> *When the system was deployed, when hitting the refresh button on the web browser (forcing with the Shift key): Did something change?*

Yes, it changes the vm serving the site.

## Q10
> *When using the Benchmarking tool, when using the concurrency parameter (-c) to a value still sustainable, What did you observe in the results of the Benchmark, were there errors, or failed request? (just a brief interpretation)*

Running the benchmark making 100 000 requests, with different concurrency levels (2, 3, 5, 10, 15, 25, 50 and 100), none of the benchmarks had failed requests/ errors, but we noticed that the time per request increased with the concurrency level.
