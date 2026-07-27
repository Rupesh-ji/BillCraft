package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BusinessProfile
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.BillViewModel

@Composable
fun BusinessProfileScreen(
    viewModel: BillViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()

    var companyName by remember(profile) { mutableStateOf(profile.companyName) }
    var tagline by remember(profile) { mutableStateOf(profile.tagline) }
    var streetAddress by remember(profile) { mutableStateOf(profile.streetAddress) }
    var cityStatePincode by remember(profile) { mutableStateOf(profile.cityStatePincode) }
    var phoneNumbers by remember(profile) { mutableStateOf(profile.phoneNumbers) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var website by remember(profile) { mutableStateOf(profile.website) }
    var gstin by remember(profile) { mutableStateOf(profile.gstin) }
    var panNumber by remember(profile) { mutableStateOf(profile.panNumber) }
    
    var bankAccountName by remember(profile) { mutableStateOf(profile.bankAccountName) }
    var bankAccountNumber by remember(profile) { mutableStateOf(profile.bankAccountNumber) }
    var bankIfsc by remember(profile) { mutableStateOf(profile.bankIfsc) }
    var bankNameBranch by remember(profile) { mutableStateOf(profile.bankNameBranch) }
    var upiId by remember(profile) { mutableStateOf(profile.upiId) }
    var defaultTerms by remember(profile) { mutableStateOf(profile.defaultTerms) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Business Profile Settings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text("Your profile details appear automatically on all generated bills & invoices.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        // Company Basic Info
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Company Identity", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company / Shop Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_company_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Business Tagline") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = streetAddress,
                        onValueChange = { streetAddress = it },
                        label = { Text("Street Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cityStatePincode,
                        onValueChange = { cityStatePincode = it },
                        label = { Text("City, State & Pincode") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = phoneNumbers,
                            onValueChange = { phoneNumbers = it },
                            label = { Text("Phone Numbers") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it },
                            label = { Text("GSTIN Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = panNumber,
                            onValueChange = { panNumber = it },
                            label = { Text("PAN Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Bank & Payment QR Settings
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Bank & Instant UPI Payment Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)

                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("UPI ID / VPA (for automatic QR Code)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_upi_id"),
                        singleLine = true,
                        placeholder = { Text("e.g. yourcompany@upi") }
                    )

                    OutlinedTextField(
                        value = bankAccountName,
                        onValueChange = { bankAccountName = it },
                        label = { Text("Bank Account Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = bankAccountNumber,
                            onValueChange = { bankAccountNumber = it },
                            label = { Text("A/C Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bankIfsc,
                            onValueChange = { bankIfsc = it },
                            label = { Text("IFSC Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = bankNameBranch,
                        onValueChange = { bankNameBranch = it },
                        label = { Text("Bank Name & Branch") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Default Terms
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Default Terms & Conditions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)

                    OutlinedTextField(
                        value = defaultTerms,
                        onValueChange = { defaultTerms = it },
                        label = { Text("Default Invoice Declaration / Terms") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updatedProfile = profile.copy(
                        companyName = companyName,
                        tagline = tagline,
                        streetAddress = streetAddress,
                        cityStatePincode = cityStatePincode,
                        phoneNumbers = phoneNumbers,
                        email = email,
                        website = website,
                        gstin = gstin,
                        panNumber = panNumber,
                        bankAccountName = bankAccountName,
                        bankAccountNumber = bankAccountNumber,
                        bankIfsc = bankIfsc,
                        bankNameBranch = bankNameBranch,
                        upiId = upiId,
                        defaultTerms = defaultTerms
                    )
                    viewModel.saveBusinessProfile(updatedProfile)
                    Toast.makeText(context, "Business Profile Saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_business_profile"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
