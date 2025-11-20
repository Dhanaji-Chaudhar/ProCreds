package com.procreds.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Entity representing AWS EKS (Elastic Kubernetes Service) configuration
 * 
 * Stores AWS EKS cluster credentials and settings including:
 * - Cluster name and region (compound unique identifier)
 * - AWS access credentials (access key and secret key)
 * - Optional namespace and role ARN
 * - Description for identification purposes
 * 
 * Collection: aws_eks_configs
 * Indexes: (clusterName, region) compound unique, region, namespace, createdAt
 * 
 * @author ProCreds Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "aws_eks_configs")
@CompoundIndex(name = "cluster_region_idx", def = "{'cluster_name': 1, 'region': 1}", unique = true)
public class AwsEksConfig extends BaseEntity {

    /**
     * EKS cluster name - part of compound unique identifier with region
     * Must be 2-50 characters, Kubernetes-compliant naming
     */
    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 50, message = "Cluster name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$", 
             message = "Cluster name must contain only lowercase letters, numbers, and hyphens")
    @Field("cluster_name")
    private String clusterName;

    /**
     * AWS access key ID for authentication
     * Must be at least 16 characters (AWS access keys are typically 20 chars)
     */
    @NotBlank(message = "Access key is required")
    @Size(min = 16, message = "Access key must be at least 16 characters")
    @Field("access_key")
    private String accessKey;

    /**
     * AWS secret access key for authentication
     * Must be at least 32 characters (AWS secret keys are typically 40 chars)
     */
    @NotBlank(message = "Secret key is required")
    @Size(min = 32, message = "Secret key must be at least 32 characters")
    @Field("secret_key")
    private String secretKey;

    /**
     * AWS region where the EKS cluster is located
     * Must be a valid AWS region format (e.g., us-east-1, eu-west-1)
     */
    @NotBlank(message = "Region is required")
    @Pattern(regexp = "^[a-z]{2}-[a-z]+-[0-9]$", 
             message = "Region must be in valid AWS format (e.g., us-east-1)")
    @Indexed
    @Field("region")
    private String region;

    /**
     * Kubernetes namespace (optional)
     * If provided, operations will be scoped to this namespace
     */
    @Pattern(regexp = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$", 
             message = "Namespace must contain only lowercase letters, numbers, and hyphens")
    @Size(max = 63, message = "Namespace cannot exceed 63 characters")
    @Indexed
    @Field("namespace")
    private String namespace;

    /**
     * AWS IAM role ARN for EKS access (optional)
     * If provided, will be used for role-based access
     */
    @Pattern(regexp = "^arn:aws:iam::[0-9]{12}:role/.+", 
             message = "Role ARN must be in valid AWS ARN format")
    @Field("role_arn")
    private String roleArn;

    /**
     * Human-readable description for this configuration
     * Helps identify the purpose or context of this credential set
     */
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Field("description")
    private String description;
}

