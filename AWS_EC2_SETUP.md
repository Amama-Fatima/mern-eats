# AWS EC2 Setup Guide for Jenkins with Docker and Selenium Tests

## Part 1: EC2 Instance Configuration (IMPORTANT - Avoid Swap Issues)

### Recommended Instance Specifications

**Based on AWS Free Tier eligibility, here are your options:**

#### Option 1: **t3.small** (RECOMMENDED for Free Tier) ✅

- 2 vCPUs
- 2 GB RAM
- **Cost**: FREE for 750 hours/month (Free Tier)
- **Caveat**: Will need swap configuration (2GB RAM is tight but workable)

#### Option 2: **m7i-flex.large** (BEST but may cost more)

- 2 vCPUs
- 8 GB RAM
- **Cost**: May exceed Free Tier limits, check pricing
- **Best option** if you can afford it - plenty of RAM, no swap needed

#### Option 3: **c7i-flex.large** (Good alternative)

- 2 vCPUs
- 4 GB RAM
- **Cost**: May exceed Free Tier limits, check pricing
- **Good option** - sufficient RAM, no swap needed

#### Option 4: **t3.micro** (NOT RECOMMENDED) ❌

- 2 vCPUs
- 1 GB RAM
- Will definitely crash with Jenkins + Docker + Chrome

**Memory Requirements:**

- Jenkins itself needs ~1GB RAM
- Docker daemon needs ~512MB RAM
- Maven build needs ~1GB RAM
- Chrome in Docker container needs ~1GB RAM
- OS overhead ~512MB RAM
- **Total: ~3.5-4GB ideal, 2GB minimum with swap**

### My Recommendation for You:

**Use t3.small with swap configuration** - it's free and will work with the swap setup I'll provide below. Yes, you'll need swap, but I'll make it painless with a single script.

### Step 1: Launch EC2 Instance

1. **Go to AWS Console** → EC2 Dashboard → Launch Instance

2. **Name and Tags**

   - Name: `Jenkins-Docker-Server`

3. **Application and OS Images (Amazon Machine Image)**

   - Select: **Ubuntu Server 22.04 LTS (HVM), SSD Volume Type**
   - Architecture: 64-bit (x86)

4. **Instance Type**

   - Select: **t3.small** ✅ (Free Tier eligible)
   - Alternative: **c7i-flex.large** or **m7i-flex.large** if budget allows
   - Do NOT select t3.micro (will crash)

5. **Key Pair (login)**

   - Create new key pair or use existing
   - Name: `jenkins-docker-key`
   - Type: RSA
   - Format: .pem (for SSH) or .ppk (for PuTTY)
   - Download and save securely

6. **Network Settings**

   - Create security group or use existing
   - Add these rules:
     - SSH (Port 22) - Source: Your IP
     - HTTP (Port 80) - Source: Anywhere (0.0.0.0/0)
     - Custom TCP (Port 8080) - Source: Anywhere (0.0.0.0/0) for Jenkins
     - Custom TCP (Port 5173) - Source: Anywhere (0.0.0.0/0) for Frontend (optional)
     - Custom TCP (Port 7000) - Source: Anywhere (0.0.0.0/0) for Backend (optional)

7. **Configure Storage**

   - Size: **20 GB minimum** (30 GB recommended for Docker images)
   - Volume Type: gp3 (General Purpose SSD)
   - Delete on Termination: Yes

8. **Advanced Details** (Optional but Recommended)

   - Enable: **Detailed CloudWatch monitoring** (helps track resource usage)

9. **Click "Launch Instance"**

---

## Part 2: Connect to EC2 Instance

### For Windows (PowerShell/Command Prompt)

```powershell
# Navigate to your key file location
cd C:\path\to\your\key

# Set proper permissions (Windows)
icacls jenkins-docker-key.pem /inheritance:r
icacls jenkins-docker-key.pem /grant:r "%username%":"(R)"

# Connect via SSH
ssh -i jenkins-docker-key.pem ubuntu@<YOUR_EC2_PUBLIC_IP>
```

### For Windows (PuTTY)

1. Open PuTTY
2. Host Name: `ubuntu@<YOUR_EC2_PUBLIC_IP>`
3. Connection → SSH → Auth → Browse to your .ppk file
4. Click Open

---

## Part 3: Install Required Software on EC2

### IMPORTANT: Configure Swap (Required for t3.small)

**If you selected t3.small, run this FIRST before anything else:**

```bash
# Create and configure 4GB swap file (one-time setup)
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Make swap permanent (survives reboot)
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# Optimize swap usage (use swap only when needed)
sudo sysctl vm.swappiness=10
echo 'vm.swappiness=10' | sudo tee -a /etc/sysctl.conf

# Verify swap is active
free -h
```

**Expected output after `free -h`:**

```text
              total        used        free
Mem:           1.9Gi       200Mi       1.5Gi
Swap:          4.0Gi         0B       4.0Gi
```

✅ **Swap configured! Now proceed with regular setup.**

---

### Update System

```bash
sudo apt update && sudo apt upgrade -y
```

### Install Java 21 (Required for Jenkins and Maven)

```bash
# Add Adoptium repository
wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo tee /usr/share/keyrings/adoptium.asc

echo "deb [signed-by=/usr/share/keyrings/adoptium.asc] https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list

sudo apt update

# Install Java 21
sudo apt install -y temurin-21-jdk

# Verify installation
java -version
```

Expected output:

```
openjdk version "21.0.x" ...
```

### Install Docker

```bash
# Install Docker
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker

# Verify Docker installation
sudo docker --version
sudo docker run hello-world
```

### Install Jenkins

```bash
# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

sudo apt update
sudo apt install -y jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Check Jenkins status
sudo systemctl status jenkins
```

### Configure Docker Permissions for Jenkins

```bash
# Add Jenkins user to Docker group
sudo usermod -aG docker jenkins

# Restart Jenkins to apply group changes
sudo systemctl restart jenkins

# Verify Jenkins can access Docker
sudo -u jenkins docker ps
```

---

## Part 4: Access Jenkins Web Interface

1. **Get Initial Admin Password**

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

2. **Open Browser**

   - Navigate to: `http://<YOUR_EC2_PUBLIC_IP>:8080`
   - Paste the initial admin password

3. **Install Suggested Plugins**

   - Select "Install suggested plugins"
   - Wait for installation to complete

4. **Create Admin User**

   - Username: `admin` (or your choice)
   - Password: (strong password)
   - Full Name: Your Name
   - Email: your.email@example.com

5. **Jenkins URL Configuration**
   - Keep default: `http://<YOUR_EC2_PUBLIC_IP>:8080`
   - Click "Save and Finish"

---

## Part 5: Install Jenkins Plugins

1. **Go to**: Manage Jenkins → Manage Plugins

2. **Install Required Plugins**:

   - **Docker Pipeline** (for Docker integration)
   - **Git Plugin** (should be pre-installed)
   - **Pipeline** (should be pre-installed)
   - **HTML Publisher** (for test reports)
   - **TestNG Results** (for TestNG reports)

3. **Click**: "Install without restart"

---

## Part 6: Configure Jenkins Credentials for GitHub

### Option A: Using GitHub Personal Access Token (Recommended)

1. **Generate GitHub Token**:

   - Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Click "Generate new token (classic)"
   - Name: `jenkins-mern-eats`
   - Select scopes: `repo` (all), `admin:repo_hook`
   - Click "Generate token"
   - **Copy the token immediately** (you won't see it again)

2. **Add to Jenkins**:
   - Go to: Manage Jenkins → Manage Credentials
   - Click: (global) → Add Credentials
   - Kind: **Username with password**
   - Username: Your GitHub username
   - Password: Paste the GitHub token
   - ID: `github-credentials`
   - Description: `GitHub Access Token`
   - Click "Create"

---

## Part 7: Verify Memory Configuration

```bash
# Check available memory
free -h

# For t3.small, you should see:
#               total        used        free
# Mem:           1.9Gi       800Mi       900Mi
# Swap:          4.0Gi        0B        4.0Gi

# For c7i-flex.large or m7i-flex.large:
#               total        used        free
# Mem:           4-8Gi       1.2Gi       2.5Gi
# Swap:          4.0Gi        0B        4.0Gi

# Check Docker is running
sudo docker info | grep -i memory

# Monitor system resources during test
sudo apt install htop -y
htop  # Press q to quit
```

---

## Part 8: Test Docker Image Pull

```bash
# Pull the Maven Chrome Docker image (this may take 5-10 minutes)
sudo docker pull markhobson/maven-chrome:latest

# Verify image is downloaded
sudo docker images

# Test run the image
sudo docker run --rm markhobson/maven-chrome:latest mvn --version
sudo docker run --rm markhobson/maven-chrome:latest google-chrome --version
```

Expected output:

```
Apache Maven 3.x.x
Google Chrome 1xx.x.xxxx.xx
```

---

## Part 9: Security Best Practices

### Configure Firewall (UFW)

```bash
# Enable firewall
sudo ufw enable

# Allow SSH
sudo ufw allow 22/tcp

# Allow Jenkins
sudo ufw allow 8080/tcp

# Check status
sudo ufw status
```

### Regular Updates

```bash
# Create update script
cat << 'EOF' | sudo tee /usr/local/bin/update-system.sh
#!/bin/bash
apt update && apt upgrade -y
apt autoremove -y
EOF

sudo chmod +x /usr/local/bin/update-system.sh

# Run weekly updates (optional)
echo "0 2 * * 0 root /usr/local/bin/update-system.sh" | sudo tee -a /etc/crontab
```

---

## Part 10: Monitoring and Troubleshooting

### Check Resource Usage

```bash
# Real-time monitoring
htop

# Memory usage
free -h

# Disk usage
df -h

# Docker stats
sudo docker stats

# Jenkins logs
sudo journalctl -u jenkins -f
```

### Common Issues and Solutions

**Issue 1: Jenkins won't start**

```bash
# Check logs
sudo journalctl -u jenkins -n 50

# Restart Jenkins
sudo systemctl restart jenkins
```

**Issue 2: Docker permission denied**

```bash
# Re-add Jenkins to Docker group
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

# Verify
sudo -u jenkins docker ps
```

**Issue 3: Out of disk space**

```bash
# Clean Docker images and containers
sudo docker system prune -a -f

# Remove old Jenkins builds
sudo find /var/lib/jenkins/workspace -type d -mtime +30 -exec rm -rf {} +
```

---

## Cost Estimation

### Option 1: t3.small (FREE with Free Tier) ✅

- **Hourly**: $0.0208 (without Free Tier)
- **Monthly with Free Tier**: **$0** (750 hours free)
- **For Assignment**: **$0** (stays within 750 hours/month)
- **Storage**: ~$2/month for 20GB EBS

### Option 2: c7i-flex.large

- **Hourly**: ~$0.08-0.10 (check AWS pricing)
- **Monthly**: ~$60-75
- **For Assignment** (1 week): ~$13-17

### Option 3: m7i-flex.large

- **Hourly**: ~$0.10-0.12 (check AWS pricing)
- **Monthly**: ~$75-90
- **For Assignment** (1 week): ~$17-20

### Cost Optimization Tips

1. **Use t3.small with Free Tier** - completely free! ✅
2. **Stop instance when not in use** (you only pay for storage ~$2/month)
3. **Set up billing alerts** in AWS Console (set alert at $5)
4. **Terminate instance after assignment** completion
5. **Monitor Free Tier usage** in AWS Billing Dashboard

---

## Quick Reference Commands

```bash
# Start/Stop Jenkins
sudo systemctl start jenkins
sudo systemctl stop jenkins
sudo systemctl restart jenkins

# Start/Stop Docker
sudo systemctl start docker
sudo systemctl stop docker

# View Jenkins logs
sudo journalctl -u jenkins -f

# Check running containers
sudo docker ps

# Check system resources
free -h
df -h
```

---

## Next Steps

After completing this setup, you're ready to:

1. Create a Jenkinsfile in your repository
2. Set up Jenkins Pipeline job
3. Configure pipeline to run Selenium tests in Docker container

See `JENKINS_PIPELINE_SETUP.md` for the next steps.
