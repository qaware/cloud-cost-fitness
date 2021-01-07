#!/bin/bash

user="billing-access-user"

policy=$(aws iam create-policy --policy-name billing-access --policy-document file://aws-billing-policy.json | jq '.Policy.Arn')
aws iam create-user --user-name $user
aws iam attach-user-policy --user-name $user --policy-arn $policy
